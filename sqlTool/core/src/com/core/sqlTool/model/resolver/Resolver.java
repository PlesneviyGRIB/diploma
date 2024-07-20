package com.core.sqlTool.model.resolver;

import com.core.sqlTool.exception.UnexpectedException;
import com.core.sqlTool.model.cache.CacheContext;
import com.core.sqlTool.model.cache.CacheStrategy;
import com.core.sqlTool.model.command.SelectCommand;
import com.core.sqlTool.model.command.WhereCommand;
import com.core.sqlTool.model.command.domain.Command;
import com.core.sqlTool.model.command.domain.ComplexCalculedCommand;
import com.core.sqlTool.model.command.domain.SimpleCalculedCommand;
import com.core.sqlTool.model.command.domain.SimpleCommand;
import com.core.sqlTool.model.command.join.JoinCommand;
import com.core.sqlTool.model.complexity.*;
import com.core.sqlTool.model.domain.ExternalHeaderRow;
import com.core.sqlTool.model.domain.HeaderRow;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.domain.Projection;
import com.core.sqlTool.model.visitor.ContextSensitiveExpressionQualifier;
import com.core.sqlTool.model.visitor.ExpressionComplexityCalculator;
import com.core.sqlTool.utils.ModelUtils;

import java.util.List;
import java.util.stream.Stream;

public record Resolver(Projection projection, CacheContext cacheContext) {

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

//                var expressionIsContextSensitive = command.getExpression()
//                        .accept(new ContextSensitiveExpressionQualifier(lazyTable.columns()));
//
//                // unsafe due to use `lazyTable.phonyHeaderRow()` that is not actual context row
//                var phonyHeaderRow = new HeaderRow(lazyTable.columns(), ModelUtils.typeSafeEmptyRow(lazyTable));
//                var calculatedExpressionEntry = command.getExpression()
//                        .accept(new ExpressionComplexityCalculator(utilityInstance(), phonyHeaderRow, externalRow))
//                        .normalize();
//
//                if (command instanceof WhereCommand where) {
//                    return new ComplexCalculatorEntry(where, calculatedExpressionEntry, expressionIsContextSensitive);
//                }
//
//                if (command instanceof JoinCommand join) {
//                    var resolverResult = utilityInstance().resolve(join.getCommands(), externalRow);
//                    return new JoinCalculatorEntry(join, resolverResult.calculator(), calculatedExpressionEntry, expressionIsContextSensitive);
//                }

                //throw new UnexpectedException();

                return null;
            }

        });
    }

    private Resolver utilityInstance() {
        return new Resolver(projection, new CacheContext(CacheStrategy.NONE));
    }

}
