package com.savchenko.sqlTool.model.command.join;

import com.savchenko.sqlTool.exception.ValidationException;
import com.savchenko.sqlTool.model.command.domain.Command;
import com.savchenko.sqlTool.model.command.domain.ComplexCalculedCommand;
import com.savchenko.sqlTool.model.complexity.SimpleCalculatorEntry;
import com.savchenko.sqlTool.model.domain.LazyTable;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.expression.Value;
import com.savchenko.sqlTool.model.resolver.CommandResult;
import com.savchenko.sqlTool.model.resolver.Resolver;
import com.savchenko.sqlTool.support.JoinStreams;
import com.savchenko.sqlTool.utils.ValidationUtils;
import org.apache.commons.collections4.ListUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.lang.String.format;

public abstract class Join extends ComplexCalculedCommand {

    private final List<Command> commands;

    private final JoinStrategy strategy;

    public Join(List<Command> commands, Expression expression, JoinStrategy strategy) {
        super(expression);
        this.commands = commands;
        this.strategy = strategy;
    }

    abstract Stream<List<Value<?>>> run(JoinStreams joinStreams);

    @Override
    public CommandResult run(LazyTable lazyTable, Projection projection, Resolver resolver) {

        var resolverResult = resolver.resolve(commands, lazyTable.externalRow());
        var joinedTable = resolverResult.lazyTable();
        var mergedColumns = ListUtils.union(lazyTable.columns(), joinedTable.columns());

        validate(lazyTable, joinedTable);
        ValidationUtils.expectBooleanValueAsResolvedType(expression, mergedColumns, lazyTable.externalRow());

        var tableName = format("%s_%s", lazyTable.name(), joinedTable.name());
        var targetDataStream = run(strategy.run(lazyTable, joinedTable, expression, resolver));

        return new CommandResult(
                new LazyTable(tableName, mergedColumns, targetDataStream, lazyTable.externalRow()),
                new SimpleCalculatorEntry(this, 0)
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

    private void validate(LazyTable table, LazyTable joinedTable) {
        if (table.name().equals(joinedTable.name())) {
            throw new ValidationException("There are two tables with the same name '%s' in context. Use alias to resolve the conflict.", table.name());
        }
    }
}
