package com.savchenko.sqlTool.query;

import com.savchenko.sqlTool.model.Builder;
import com.savchenko.sqlTool.model.command.*;
import com.savchenko.sqlTool.model.operation.Expression;
import com.savchenko.sqlTool.repository.Projection;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Query implements Builder<List<Command>> {
    private final Projection projection;
    private final List<Command> commands = new LinkedList<>();

    private Query(Projection projection) {
        this.projection = projection;
    }

    public static Query create(Projection projection) {
        return new Query(projection);
    }

    public Query selectAll() {
        commands.add(new Select());
        return this;
    }

    public Query select(ColumnRef... columnRefs) {
        var columns = Arrays.stream(columnRefs)
                .map(c -> projection.getByName(c.table()).getColumn(c.table(), c.name()))
                .toList();
        commands.add(new Select(columns));
        return this;
    }

    public Query from(String... tables) {
        commands.add(new From(Arrays.stream(tables).toList()));
        return this;
    }

    public Query where(Expression expression) {
        commands.add(new Where(expression));
        return this;
    }

    public Query orderBy(OrderRef... orderRefs) {
        var orders = Arrays.stream(orderRefs).map(order -> {
            var columnRef = order.columnRef;
            var column = projection.getByName(columnRef.table()).getColumn(columnRef.table(), columnRef.name());
            return new Order(column, order.reverse);
        }).toList();
        commands.add(new OrderBy(orders));
        return this;
    }

    public Query limit(Integer limit) {
        commands.add(new Limit(limit));
        return this;
    }

    @Override
    public List<Command> build() {
        return commands;
    }
}