package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.exception.ValidationException;
import com.savchenko.sqlTool.model.command.domain.SimpleCalculedCommand;
import com.savchenko.sqlTool.model.complexity.SimpleCalculatorEntry;
import com.savchenko.sqlTool.model.domain.LazyTable;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Row;
import com.savchenko.sqlTool.model.expression.StringValue;
import com.savchenko.sqlTool.model.expression.Value;
import com.savchenko.sqlTool.model.resolver.CommandResult;
import com.savchenko.sqlTool.utils.ModelUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class OrderBy implements SimpleCalculedCommand {

    private final List<Order> orders;

    public OrderBy(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public CommandResult run(LazyTable lazyTable, Projection projection) {

        orders.stream()
                .collect(Collectors.groupingBy(Order::column, Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .findAny().ifPresent(column -> {
                    throw new ValidationException("ORDER_BY command can not contains the same column (%s) several times!", column);
                });

        var indexes = orders.stream()
                .map(o -> ModelUtils.resolveColumnIndex(lazyTable.columns(), o.column()))
                .toList();

        var complexityCollector = new Object() {
            public Integer complexity = 0;
        };

        Comparator<Row> rowsComparator = (row1, row2) -> {
            for (int i = 0; i < indexes.size(); i++) {
                var idx = indexes.get(i);
                var elem1 = row1.values().get(idx);
                var elem2 = row2.values().get(idx);

                var res = ModelUtils.compareValues(elem1, elem2, lazyTable.columns().get(idx).type());
                complexityCollector.complexity += getComparisonComplexity(elem1, elem2);

                if (orders.get(i).reverse()) {
                    res *= -1;
                }
                if (res != 0) {
                    return res;
                }
            }
            return 0;
        };

        return new CommandResult(
                new LazyTable(lazyTable.name(), lazyTable.columns(), lazyTable.dataStream().sorted(rowsComparator), lazyTable.externalRow()),
                new SimpleCalculatorEntry(this, complexityCollector.complexity)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderBy orderBy = (OrderBy) o;
        return Objects.equals(orders, orderBy.orders);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(orders);
    }

    private Integer getComparisonComplexity(Value<?> value1, Value<?> value2) {
        if (value1 instanceof StringValue v1 && value2 instanceof StringValue v2) {
            return Math.min(v1.value().length(), v2.value().length());
        }
        return 1;
    }
}
