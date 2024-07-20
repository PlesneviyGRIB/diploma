package com.core.sqlTool.model.command;

import com.core.sqlTool.exception.ValidationException;
import com.core.sqlTool.model.command.domain.SimpleCalculedCommand;
import com.core.sqlTool.model.complexity.CalculatorEntry;
import com.core.sqlTool.model.domain.Column;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.domain.Projection;
import com.core.sqlTool.model.domain.Row;
import com.core.sqlTool.model.expression.StringValue;
import com.core.sqlTool.model.expression.Value;
import com.core.sqlTool.utils.ModelUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public record OrderByCommand(List<Pair<Column, Boolean>> orders) implements SimpleCalculedCommand {

    @Override
    public LazyTable run(LazyTable lazyTable, Projection projection, CalculatorEntry calculatorEntry) {

//        orders.stream()
//                .collect(Collectors.groupingBy(OrderCommand::column, Collectors.counting()))
//                .entrySet().stream()
//                .filter(entry -> entry.getValue() > 1)
//                .map(Map.Entry::getKey)
//                .findAny().ifPresent(column -> {
//                    throw new ValidationException("ORDER_BY command can not contains the same columnName (%s) several times!", column);
//                });
//
//        var indexes = orders.stream()
//                .map(o -> ModelUtils.resolveColumnIndex(lazyTable.columns(), o.column()))
//                .toList();
//
//        Comparator<Row> rowsComparator = (row1, row2) -> {
//            for (int i = 0; i < indexes.size(); i++) {
//                var idx = indexes.get(i);
//                var elem1 = row1.values().get(idx);
//                var elem2 = row2.values().get(idx);
//
//                var res = ModelUtils.compareValues(elem1, elem2, lazyTable.columns().get(idx).columnType());
//
//                IntStream.range(0, getComparisonComplexity(elem1, elem2)).forEach(calculatorEntry::count);
//
//                if (orders.get(i).reverse()) {
//                    res *= -1;
//                }
//                if (res != 0) {
//                    return res;
//                }
//            }
//            return 0;
//        };
//
//        return new LazyTable(lazyTable.name(), lazyTable.columns(), lazyTable.dataStream().sorted(rowsComparator), lazyTable.externalRow());

        return null;
    }

    private Integer getComparisonComplexity(Value<?> value1, Value<?> value2) {
        if (value1 instanceof StringValue v1 && value2 instanceof StringValue v2) {
            return Math.min(v1.value().length(), v2.value().length());
        }
        return 1;
    }
}
