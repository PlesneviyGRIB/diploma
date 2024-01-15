package com.savchenko.sqlTool.model.operation;

import com.savchenko.sqlTool.model.Table;
import com.savchenko.sqlTool.model.operation.supportive.Order;
import com.savchenko.sqlTool.repository.Projection;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class OrderBy implements Operation {
    private final List<Order> orders;

    public OrderBy(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public Table run(Table table, Projection projection) {
        //TODO implement multiple orders in right way

        var indexes = orders.stream().map(o -> {
            var cls = table.columns();
            var cl = cls.stream().filter(column -> column.equals(o.column())).findFirst()
                    .orElseThrow(() -> new RuntimeException(String.format("Unable to find %s in [%s]",
                            o.column(), cls.stream().map(Objects::toString).collect(Collectors.joining(", ")))));
            return cls.indexOf(cl);
        }).toList();

        Comparator<List<String>> rowsComparator = (row1, row2) -> {
            for (int i = 0; i < indexes.size(); i++) {
                var idx = indexes.get(i);
                var res = row1.get(idx).compareTo(row2.get(idx));
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
}
