package com.core.sqlTool.model.command.join;

import com.core.sqlTool.exception.ValidationException;
import com.core.sqlTool.model.command.domain.Command;
import com.core.sqlTool.model.command.domain.ComplexCalculedCommand;
import com.core.sqlTool.model.complexity.CalculatorEntry;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.domain.Projection;
import com.core.sqlTool.model.domain.Row;
import com.core.sqlTool.model.expression.Expression;
import com.core.sqlTool.model.resolver.Resolver;
import com.core.sqlTool.support.JoinStreams;
import com.core.sqlTool.utils.ModelUtils;
import com.core.sqlTool.utils.ValidationUtils;
import org.apache.commons.collections4.ListUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.lang.String.format;

public abstract class JoinCommand extends ComplexCalculedCommand {

    private final List<Command> commands;

    private final JoinStrategy strategy;

    public JoinCommand(List<Command> commands, Expression expression, JoinStrategy strategy) {
        super(expression);
        this.commands = commands;
        this.strategy = strategy;
    }

    abstract Stream<Row> run(JoinStreams joinStreams);

    @Override
    public LazyTable run(LazyTable lazyTable, Projection projection, Resolver resolver, CalculatorEntry calculatorEntry) {

        var resolverResult = resolver.resolve(commands, lazyTable.externalRow());
        var joinedTable = resolverResult.lazyTable();
        var mergedColumns = ListUtils.union(lazyTable.columns(), joinedTable.columns());

        validate(lazyTable, joinedTable);
        ValidationUtils.expectBooleanValueAsResolvedType(expression, mergedColumns, lazyTable.externalRow());

        var tableName = format("%s_%s", lazyTable.name(), joinedTable.name());
        var targetDataStream = run(strategy.run(lazyTable, joinedTable, expression, resolver, calculatorEntry));
        var targetTable = new LazyTable(tableName, mergedColumns, targetDataStream, lazyTable.externalRow());

        return ModelUtils.renameTable(targetTable, tableName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JoinCommand join = (JoinCommand) o;
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

    public List<Command> getCommands() {
        return commands;
    }
}
