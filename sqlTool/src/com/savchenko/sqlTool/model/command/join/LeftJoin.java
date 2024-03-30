package com.savchenko.sqlTool.model.command.join;

import com.savchenko.sqlTool.model.command.domain.Command;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.expression.Value;
import com.savchenko.sqlTool.utils.ModelUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class LeftJoin extends Join {
    public LeftJoin(List<Command> commands, Expression expression, JoinStrategy strategy, Projection projection) {
        super(commands, expression, strategy, projection);
    }

    @Override
    public Table run(Table table, Table joinedTable, Supplier<Triple<List<List<Value<?>>>, Set<Integer>, Set<Integer>>> strategyExecutionResultSupplier) {

        var columns = ListUtils.union(table.columns(), joinedTable.columns());

        var result = strategyExecutionResultSupplier.get();

        var remainder = ModelUtils.getIndexedData(table.data()).stream()
                .filter(pair -> !result.getMiddle().contains(pair.getLeft()))
                .map(pair -> ListUtils.union(pair.getRight(), ModelUtils.emptyRow(joinedTable)))
                .toList();

        var data = ListUtils.union(result.getLeft(), remainder);

        return new Table(null, columns, data, List.of());
    }

}
