package com.savchenko.sqlTool.query;

import com.savchenko.sqlTool.model.Builder;
import com.savchenko.sqlTool.model.command.*;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.structure.Column;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Query implements Builder<List<Command>> {
    private final List<Command> commands = new LinkedList<>();

    public static Query create() {
        return new Query();
    }

    public Query selectAll() {
        commands.add(new Select());
        return this;
    }

    public Query select(Column... columns) {
        commands.add(new Select(Arrays.asList(columns)));
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

    public Query orderBy(Order... orders) {
        commands.add(new OrderBy(Arrays.asList(orders)));
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