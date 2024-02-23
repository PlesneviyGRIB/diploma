package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.exception.ValidationException;
import com.savchenko.sqlTool.model.expression.NullValue;
import com.savchenko.sqlTool.model.expression.Value;
import com.savchenko.sqlTool.model.structure.Table;
import com.savchenko.sqlTool.repository.Projection;
import com.savchenko.sqlTool.utils.ModelUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class OrderBy extends Command {
    private final List<Order> orders;

    public OrderBy(List<Order> orders, Projection projection) {
        super(projection);
        this.orders = orders;
    }

    @Override
    public Table run(Table table) {
        var indexes = orders.stream()
                .map(o -> ModelUtils.resolveColumnIndex(table.columns(), o.column()))
                .toList();

        Comparator<? super List<Value<?>>> rowsComparator = (row1, row2) -> {
            for (int i = 0; i < indexes.size(); i++) {
                var idx = indexes.get(i);
                var elem1 = row1.get(idx);
                var elem2 = row2.get(idx);

                var res = ModelUtils.compareValues(elem1, elem2, table.columns().get(idx).type());

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
        return new Table(table.name(), table.columns(), data, List.of());
    }

    @Override
    public void validate(Table table) {
        orders.forEach(o -> {
            var t = projection.getByName(o.column().table());
            ModelUtils.resolveColumn(t.columns(), o.column());
        });
        orders.stream()
                .collect(Collectors.groupingBy(Order::column, Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .findAny().ifPresent(column -> {
                    throw new ValidationException("ORDER_BY command can not contains the same column (%s) several times!", column);
                });
    }
}
