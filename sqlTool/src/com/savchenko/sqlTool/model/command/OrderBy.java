package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.Table;
import com.savchenko.sqlTool.model.command.supportive.Order;
import com.savchenko.sqlTool.repository.Projection;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class OrderBy implements Command {
    private final List<Order> orders;

    public OrderBy(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public Table run(Table table, Projection projection) {
        var indexes = orders.stream().map(o -> {
            var cls = table.columns();
            var cl = table.getColumnByName(o.column().name());
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
}
