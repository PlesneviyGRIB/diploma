package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.exception.ValidationException;
import com.savchenko.sqlTool.model.command.domain.SimpleCalculedCommand;
import com.savchenko.sqlTool.model.complexity.SimpleCalculatorEntry;
import com.savchenko.sqlTool.model.complexity.laziness.LazyConcealer;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.model.expression.Value;
import com.savchenko.sqlTool.model.resolver.CommandResult;
import com.savchenko.sqlTool.utils.ModelUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class OrderBy implements SimpleCalculedCommand, LazyConcealer {

    private final List<Order> orders;

    public OrderBy(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public CommandResult run(Table table, Projection projection) {

        orders.stream()
                .collect(Collectors.groupingBy(Order::column, Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .findAny().ifPresent(column -> {
                    throw new ValidationException("ORDER_BY command can not contains the same column (%s) several times!", column);
                });

        var indexes = orders.stream()
                .map(o -> ModelUtils.resolveColumnIndex(table.columns(), o.column()))
                .toList();

        Comparator<? super List<Value<?>>> rowsComparator = (row1, row2) -> {
            for (int i = 0; i < indexes.size(); i++) {
                var idx = indexes.get(i);
                var elem1 = row1.get(idx);
                var elem2 = row2.get(idx);

                var res = ModelUtils.compareValues(elem1, elem2, table.columns().get(idx).type());

                if (orders.get(i).reverse()) {
                    res *= -1;
                }
                if (res != 0) {
                    return res;
                }
            }
            return 0;
        };

        var data = table.data().stream().sorted(rowsComparator).toList();
        return new CommandResult(
                new Table(table.name(), table.columns(), data, table.externalRow()),
                new SimpleCalculatorEntry(this, 0)
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
}
