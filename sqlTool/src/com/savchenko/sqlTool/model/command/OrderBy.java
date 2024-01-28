package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.structure.Table;
import com.savchenko.sqlTool.repository.Projection;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class OrderBy implements Command {
    private final List<Order> orders;

    public OrderBy(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public Table run(Table table, Projection projection) {
        var indexes = orders.stream().map(o -> {
            var cls = table.columns();
            var cl = table.getColumn(o.column());
            return cls.indexOf(cl);
        }).toList();

        Comparator<List<Comparable<?>>> rowsComparator = (row1, row2) -> {
            for (int i = 0; i < indexes.size(); i++) {
                var idx = indexes.get(i);
                var elem1 = row1.get(idx);
                var elem2 = row2.get(idx);
                var targetClass = elem1.getClass();
                var res = targetClass.cast(elem1).compareTo(targetClass.cast(elem2));
                if(orders.get(i).reverse()) {
                    res *= -1;
                }
                if(res != 0) {
                    return res;
                }
            }
            return 0;
        };

        var data = table.data().stream().sorted(rowsComparator).toList();
        return new Table(table.name(), table.columns(), data);
    }

    @Override
    public void validate(Projection projection) {
        orders.forEach(o -> projection.getByName(o.column().table()).getColumn(o.column().table(), o.column().name()));
        orders.stream()
                .collect(Collectors.groupingBy(Order::column, Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .findAny().ifPresent(column -> {
                    throw new RuntimeException(format("ORDER_BY command can not contains the same column (%s) several times!", column));
                });
    }
}
