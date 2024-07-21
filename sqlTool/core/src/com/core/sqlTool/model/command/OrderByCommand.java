package com.core.sqlTool.model.command;

import com.core.sqlTool.model.complexity.CalculatorEntry;
import com.core.sqlTool.model.domain.Column;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.domain.Projection;
import com.core.sqlTool.model.domain.Row;
import com.core.sqlTool.utils.ModelUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public record OrderByCommand(List<Pair<Column, Boolean>> orders) implements SimpleCalculatedCommand {

    @Override
    public LazyTable run(LazyTable lazyTable, Projection projection, CalculatorEntry calculatorEntry) {

        var indexes = orders.stream()
                .map(order -> ModelUtils.resolveColumnIndex(lazyTable.columns(), order.getLeft()))
                .toList();

        Comparator<Row> rowsComparator = (row1, row2) -> IntStream.range(0, indexes.size())
                .map(i -> {
                    var index = indexes.get(i);
                    var elem1 = row1.values().get(index);
                    var elem2 = row2.values().get(index);

                    var res = ModelUtils.compareValues(elem1, elem2, lazyTable.columns().get(index).getColumnType());
                    if (!orders.get(i).getRight()) {
                        res *= -1;
                    }
                    return res;
                })
                .filter(res -> res != 0)
                .findFirst().orElse(0);

        return new LazyTable(lazyTable.name(), lazyTable.columns(), lazyTable.dataStream().sorted(rowsComparator), lazyTable.externalRow());
    }

}
