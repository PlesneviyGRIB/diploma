package com.core.sqlTool.model.command.join;

import com.core.sqlTool.exception.ValidationException;
import com.core.sqlTool.model.command.Command;
import com.core.sqlTool.model.command.SingleExpressionCommand;
import com.core.sqlTool.model.complexity.CalculatorEntry;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.domain.Projection;
import com.core.sqlTool.model.domain.Row;
import com.core.sqlTool.model.expression.Expression;
import com.core.sqlTool.model.resolver.Resolver;
import com.core.sqlTool.support.JoinStreams;
import com.core.sqlTool.utils.ModelUtils;
import com.core.sqlTool.utils.ValidationUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;

import java.util.List;
import java.util.stream.Stream;

import static java.lang.String.format;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public abstract class JoinCommand implements SingleExpressionCommand {

    private final List<Command> commands;

    private final Expression expression;

    private final JoinStrategy strategy;

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

    private void validate(LazyTable table, LazyTable joinedTable) {
        if (table.name().equals(joinedTable.name())) {
            throw new ValidationException("There are two tables with the same columnName '%s' in context. Use alias to resolve the conflict.", table.name());
        }
    }

}
