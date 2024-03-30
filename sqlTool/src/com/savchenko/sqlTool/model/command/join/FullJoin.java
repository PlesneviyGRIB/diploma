package com.savchenko.sqlTool.model.command.join;

import com.savchenko.sqlTool.model.command.domain.Command;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.expression.Value;
import com.savchenko.sqlTool.utils.ModelUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class FullJoin extends Join {
    public FullJoin(List<Command> commands, Expression expression, JoinStrategy strategy) {
        super(commands, expression, strategy);
    }

    @Override
    public Table run(Table table, Table joinedTable, Supplier<Triple<List<List<Value<?>>>, Set<Integer>, Set<Integer>>> strategyExecutionResultSupplier) {

        var result = strategyExecutionResultSupplier.get();

        var leftRemainder = ModelUtils.getIndexedData(table.data()).stream()
                .filter(pair -> !result.getMiddle().contains(pair.getLeft()))
                .map(pair -> ListUtils.union(pair.getRight(), ModelUtils.emptyRow(joinedTable)))
                .toList();

        var rightRemainder = ModelUtils.getIndexedData(joinedTable.data()).stream()
                .filter(pair -> !result.getRight().contains(pair.getLeft()))
                .map(pair -> ListUtils.union(ModelUtils.emptyRow(table), pair.getRight()))
                .toList();

        var data = Stream.of(result.getLeft(), leftRemainder, rightRemainder).flatMap(Collection::stream).toList();

        var mergedColumns = ListUtils.union(table.columns(), joinedTable.columns());

        return new Table(null, mergedColumns, data, List.of());
    }
}
