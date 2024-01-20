package com.savchenko.sqlTool.model.query;

import com.savchenko.sqlTool.model.Builder;
import com.savchenko.sqlTool.model.Column;
import com.savchenko.sqlTool.model.command.*;
import com.savchenko.sqlTool.model.command.supportive.Order;
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

    public Query select(List<String> columns) {
        commands.add(new Select(columns));
        return this;
    }

    public Query from(String... tables) {
        commands.add(new From(Arrays.stream(tables).toList()));
        return this;
    }

    public Query where(String... predicates) {
        return this;
    }

    public Query orderBy(String... orders) {
        var orderList = Arrays.stream(orders).map(order -> {
            var tokens = order.split("\\.");
            var column = projection.getByName(tokens[0]).getColumnByName(tokens[1]);
            var asp = false;
            if(tokens.length > 2) {
                var aspect = tokens[2];
                if(!aspect.equals("asc") && !aspect.equals("desc")) {
                    throw new RuntimeException(String.format("Wrong order aspect! Value '%s' out of values [asc, desc]", aspect));
                }
                asp = aspect.equals("desc");
            }
            return new Order(column, asp);
        }).toList();
        commands.add(new OrderBy(orderList));
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