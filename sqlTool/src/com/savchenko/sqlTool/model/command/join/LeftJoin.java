package com.savchenko.sqlTool.model.command.join;

import com.savchenko.sqlTool.model.command.domain.Command;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.expression.Value;
import com.savchenko.sqlTool.utils.ModelUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class LeftJoin extends Join {
    public LeftJoin(List<Command> commands, Expression expression, JoinStrategy strategy) {
        super(commands, expression, strategy);
    }

    @Override
    public Pair<Table, Integer> run(Table table,
                                    Table joinedTable,
                                    Supplier<Triple<Stream<List<Value<?>>>,
                                            Set<Integer>, Set<Integer>>> strategyExecutionResultSupplier
    ) {

        var columns = ListUtils.union(table.columns(), joinedTable.columns());

        var result = strategyExecutionResultSupplier.get();

        var remainder = ModelUtils.getIndexedData(table.dataStream())
                .filter(pair -> !result.getMiddle().contains(pair.getLeft()))
                .map(pair -> ListUtils.union(pair.getRight(), ModelUtils.emptyRow(joinedTable)));

        var dataStream = Stream.concat(result.getLeft(), remainder);

        return Pair.of(new Table(null, columns, dataStream, table.externalRow()), remainder.toList().size());
    }

}
