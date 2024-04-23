package com.savchenko.sqlTool.model.command.join;

import com.savchenko.sqlTool.exception.ValidationException;
import com.savchenko.sqlTool.model.command.domain.Command;
import com.savchenko.sqlTool.model.command.domain.ComplexCalculedCommand;
import com.savchenko.sqlTool.model.complexity.ExecutedCalculatorEntry;
import com.savchenko.sqlTool.model.complexity.JoinCalculatorEntry;
import com.savchenko.sqlTool.model.complexity.laziness.Lazy;
import com.savchenko.sqlTool.model.domain.LazyTable;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.expression.Value;
import com.savchenko.sqlTool.model.resolver.CommandResult;
import com.savchenko.sqlTool.model.resolver.Resolver;
import com.savchenko.sqlTool.model.visitor.ContextSensitiveExpressionQualifier;
import com.savchenko.sqlTool.model.visitor.ExpressionComplexityCalculator;
import com.savchenko.sqlTool.model.visitor.ExpressionValidator;
import com.savchenko.sqlTool.utils.ModelUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.lang.String.format;

public abstract class Join extends ComplexCalculedCommand implements Lazy {

    private final List<Command> commands;

    private final JoinStrategy strategy;

    public Join(List<Command> commands, Expression expression, JoinStrategy strategy) {
        super(expression);
        this.commands = commands;
        this.strategy = strategy;
    }

    abstract Pair<LazyTable, Integer> run(LazyTable lazyTable,
                                          LazyTable joinedLazyTable,
                                          Supplier<Triple<Stream<List<Value<?>>>, Set<Integer>, Set<Integer>>> strategyExecutionResultSupplier);

    @Override
    public CommandResult run(LazyTable lazyTable, Projection projection, Resolver resolver) {

        var resolverResult = resolver.resolve(commands, lazyTable.externalRow());
        var joinedTable = resolverResult.lazyTable();

        if (lazyTable.name().equals(joinedTable.name())) {
            throw new ValidationException("There are two tables with the same name '%s' in context. Use alias to resolve the conflict.", lazyTable.name());
        }

        var mergedColumns = ListUtils.union(lazyTable.columns(), joinedTable.columns());

        expression.accept(new ExpressionValidator(mergedColumns, lazyTable.externalRow()));

        Supplier<Triple<Stream<List<Value<?>>>, Set<Integer>, Set<Integer>>> strategyExecutionResultSupplier = () -> strategy.run(lazyTable, joinedTable, expression, resolver);

        Function<Integer, ExecutedCalculatorEntry> toCalculatorEntry = remainderSize -> {

            var operationsCount = strategy.getStrategyComplexity(lazyTable, joinedTable);
            var calculedExpressionEntry = expression.accept(new ExpressionComplexityCalculator(resolver, lazyTable.externalRow())).normalize();
            var isContextSensitiveExpression = expression.accept(new ContextSensitiveExpressionQualifier(lazyTable.columns()));

            return new JoinCalculatorEntry(this, resolverResult.calculator(), remainderSize, calculedExpressionEntry, operationsCount, isContextSensitiveExpression);
        };

        var pair = run(lazyTable, joinedTable, strategyExecutionResultSupplier);
        var tableName = format("%s_%s", lazyTable.name(), joinedTable.name());

        return new CommandResult(
                ModelUtils.renameTable(pair.getLeft(), tableName),
                toCalculatorEntry.apply(pair.getRight())
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Join join = (Join) o;
        return Objects.equals(commands, join.commands) && strategy == join.strategy;
    }

    @Override
    public int hashCode() {
        return Objects.hash(commands, strategy);
    }

    public JoinStrategy getStrategy() {
        return strategy;
    }
}
