package com.savchenko.sqlTool.model.command.join;

import com.savchenko.sqlTool.model.command.domain.Command;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.expression.Value;
import com.savchenko.sqlTool.utils.ModelUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LeftJoin extends Join {
    public LeftJoin(List<Command> commands, Expression expression, JoinStrategy strategy) {
        super(commands, expression, strategy);
    }

    @Override
    public Table run(Table table,
                     Table joinedTable,
                     Supplier<Triple<List<List<Value<?>>>, Set<Integer>, Set<Integer>>> strategyExecutionResultSupplier,
                     Consumer<Integer> remainderSizeConsumer) {

        var columns = ListUtils.union(table.columns(), joinedTable.columns());

        var result = strategyExecutionResultSupplier.get();

        var remainder = ModelUtils.getIndexedData(table.data()).stream()
                .filter(pair -> !result.getMiddle().contains(pair.getLeft()))
                .map(pair -> ListUtils.union(pair.getRight(), ModelUtils.emptyRow(joinedTable)))
                .toList();

        remainderSizeConsumer.accept(remainder.size());

        var data = ListUtils.union(result.getLeft(), remainder);

        return new Table(null, columns, data, table.externalRow());
    }

}
