package com.savchenko.sqlTool.model.query;

import com.savchenko.sqlTool.model.Builder;
import com.savchenko.sqlTool.model.Column;
import com.savchenko.sqlTool.model.operation.*;
import com.savchenko.sqlTool.model.operation.supportive.Order;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Query implements Builder<List<Operation>> {


    private final List<Operation> operations = new LinkedList<>();

    private Query() {
    }

    public static Query create() {
        return new Query();
    }

    public Query select(List<String> columns) {
        operations.add(new Select(columns));
        return this;
    }

    public Query from(String... tables) {
        operations.add(new From(Arrays.stream(tables).toList()));
        return this;
    }

    public Query orderBy(String... orders) {
        var orderList = Arrays.stream(orders).map(order -> {
            var tokens = order.split("\\.");
            var column = new Column(tokens[1], tokens[0]);
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
        operations.add(new OrderBy(orderList));
        return this;
    }

    public Query limit(Integer limit) {
        operations.add(new Limit(limit));
        return this;
    }

    @Override
    public List<Operation> build() {
        return operations;
    }
}