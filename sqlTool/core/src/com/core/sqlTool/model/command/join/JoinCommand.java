package com.core.sqlTool.model.command.join;

import com.core.sqlTool.exception.TableSpecifiedTwiceException;
import com.core.sqlTool.model.command.Command;
import com.core.sqlTool.model.command.SingleExpressionCommand;
import com.core.sqlTool.model.complexity.CalculatorEntry;
import com.core.sqlTool.model.domain.Column;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.domain.Projection;
import com.core.sqlTool.model.domain.Row;
import com.core.sqlTool.model.expression.Expression;
import com.core.sqlTool.model.resolver.Resolver;
import com.core.sqlTool.support.JoinStreams;
import com.core.sqlTool.utils.ValidationUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        var targetDataStream = run(strategy.run(lazyTable, joinedTable, expression, resolver, calculatorEntry));

        return new LazyTable(null, mergedColumns, targetDataStream, lazyTable.externalRow());
    }

    private void validate(LazyTable table, LazyTable joinedTable) {

        var referencedTableNames = table.columns().stream().map(Column::getTableName).collect(Collectors.toSet());
        var referencedJoinedTableNames = joinedTable.columns().stream().map(Column::getTableName).collect(Collectors.toSet());

        var tableNamesIntersection = SetUtils.intersection(referencedTableNames, referencedJoinedTableNames);

        if (!tableNamesIntersection.isEmpty()) {
            throw new TableSpecifiedTwiceException(tableNamesIntersection.iterator().next());
        }
    }

}
