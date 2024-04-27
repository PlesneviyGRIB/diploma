package com.savchenko.sqlTool.model.resolver;

import com.savchenko.sqlTool.exception.UnexpectedException;
import com.savchenko.sqlTool.model.cache.CacheContext;
import com.savchenko.sqlTool.model.cache.CacheStrategy;
import com.savchenko.sqlTool.model.command.Where;
import com.savchenko.sqlTool.model.command.domain.Command;
import com.savchenko.sqlTool.model.command.domain.ComplexCalculedCommand;
import com.savchenko.sqlTool.model.command.domain.SimpleCalculedCommand;
import com.savchenko.sqlTool.model.command.domain.SimpleCommand;
import com.savchenko.sqlTool.model.command.join.Join;
import com.savchenko.sqlTool.model.complexity.*;
import com.savchenko.sqlTool.model.domain.ExternalHeaderRow;
import com.savchenko.sqlTool.model.domain.LazyTable;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.visitor.ContextSensitiveExpressionQualifier;
import com.savchenko.sqlTool.model.visitor.ExpressionComplexityCalculator;
import com.savchenko.sqlTool.query.Query;

import java.util.List;
import java.util.stream.Stream;

public class Resolver {

    private final Projection projection;

    private final CacheContext cacheContext;

    public Resolver(Projection projection, CacheContext cacheContext) {
        this.projection = projection;
        this.cacheContext = cacheContext;
    }

    public ResolverResult resolve(Query query) {
        return resolve(query.build(), ExternalHeaderRow.empty());
    }

    public ResolverResult resolve(List<Command> commands, ExternalHeaderRow externalRow) {

        var calculator = new Calculator();

        var targetTable = commands.stream().reduce(
                new LazyTable(null, List.of(), Stream.of(), externalRow),
                (table, command) -> {

                    var calculatorEntry = getCalculatorEntry(command, table, externalRow);
                    var resultTable = runCommand(command, table, calculatorEntry);

                    calculator.log(calculatorEntry);

                    return resultTable;
                },
                (t1, t2) -> t2
        );

        return new ResolverResult(targetTable, calculator);
    }

    private LazyTable runCommand(Command command, LazyTable lazyTable, CalculatorEntry calculatorEntry) {

        return command.accept(new Command.Visitor<>() {

            @Override
            public LazyTable visit(SimpleCommand command) {
                return command.run(lazyTable, projection);
            }

            @Override
            public LazyTable visit(SimpleCalculedCommand command) {
                return command.run(lazyTable, projection, calculatorEntry);
            }

            @Override
            public LazyTable visit(ComplexCalculedCommand command) {
                return command.run(lazyTable, projection, Resolver.this, calculatorEntry);
            }

        });
    }

    private CalculatorEntry getCalculatorEntry(Command command, LazyTable lazyTable, ExternalHeaderRow externalRow) {

        return command.accept(new Command.Visitor<>() {

            @Override
            public CalculatorEntry visit(SimpleCommand command) {
                return new SimpleEntry(command);
            }

            @Override
            public CalculatorEntry visit(SimpleCalculedCommand command) {
                return new SimpleCalculatorEntry(command);
            }

            @Override
            public CalculatorEntry visit(ComplexCalculedCommand command) {

                var expressionIsContextSensitive = command.getExpression()
                        .accept(new ContextSensitiveExpressionQualifier(lazyTable.columns()));

                var calculatedExpressionEntry = command.getExpression()
                        .accept(new ExpressionComplexityCalculator(utilityInstance(), lazyTable.phonyHeaderRow(), externalRow))
                        .normalize();

                if (command instanceof Where where) {
                    return new ComplexCalculatorEntry(where, calculatedExpressionEntry, expressionIsContextSensitive);
                }

                if (command instanceof Join join) {
                    var resolverResult = utilityInstance().resolve(join.getCommands(), externalRow);
                    return new JoinCalculatorEntry(join, resolverResult.calculator(), calculatedExpressionEntry, expressionIsContextSensitive);
                }

                throw new UnexpectedException();
            }

        });
    }

    private Resolver utilityInstance() {
        return new Resolver(projection, new CacheContext(CacheStrategy.NONE));
    }

}
