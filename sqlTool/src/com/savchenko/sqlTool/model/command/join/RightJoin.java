package com.savchenko.sqlTool.model.command.join;

import com.savchenko.sqlTool.model.command.domain.Command;
import com.savchenko.sqlTool.model.domain.LazyTable;
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

public class RightJoin extends Join {

    public RightJoin(List<Command> commands, Expression expression, JoinStrategy strategy) {
        super(commands, expression, strategy);
    }

    @Override
    public Pair<LazyTable, Integer> run(LazyTable lazyTable,
                                        LazyTable joinedLazyTable,
                                        Supplier<Triple<Stream<List<Value<?>>>,
                                            Set<Integer>, Set<Integer>>> strategyExecutionResultSupplier
    ) {

        var columns = ListUtils.union(lazyTable.columns(), joinedLazyTable.columns());

        var result = strategyExecutionResultSupplier.get();

        var remainder = ModelUtils.getIndexedData(joinedLazyTable.dataStream())
                .filter(pair -> !result.getRight().contains(pair.getLeft()))
                .map(pair -> ListUtils.union(ModelUtils.emptyRow(lazyTable), pair.getRight()));

        var data = Stream.concat(result.getLeft(), remainder);

        return Pair.of(new LazyTable(null, columns, data, lazyTable.externalRow()), remainder.toList().size());
    }
}