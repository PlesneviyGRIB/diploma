package com.savchenko.sqlTool.query;

import com.savchenko.sqlTool.model.Builder;
import com.savchenko.sqlTool.model.command.*;
import com.savchenko.sqlTool.model.expression.BinaryOperation;
import com.savchenko.sqlTool.model.expression.BooleanValue;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.operator.Operator;
import com.savchenko.sqlTool.model.structure.Column;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Query implements Builder<List<Command>> {
    private final List<Command> commands = new LinkedList<>();

    public static Query create() {
        return new Query();
    }

    public Query select(Column... columns) {
        commands.add(new Select(Arrays.asList(columns)));
        return this;
    }

    public Query from(String... tables) {
        commands.add(new From(Arrays.stream(tables).toList()));
        return this;
    }

    public Query innerJoin(String table, Expression<?> expression) {
        commands.add(new InnerJoin(table, expression));
        return this;
    }

    public Query leftJoin(String table, Expression<?> expression) {
        commands.add(new LeftJoin(table, expression));
        return this;
    }

    public Query rightJoin(String table, Expression<?> expression) {
        commands.add(new RightJoin(table, expression));
        return this;
    }

    public Query fullJoin(String table, Expression<?> expression) {
        commands.add(new FullJoin(table, expression));
        return this;
    }

    public Query where(Expression<?>... expressions) {
        var expression = Arrays.stream(expressions).reduce(new BooleanValue(true), (p, c) -> new BinaryOperation(Operator.AND, p, c));
        commands.add(new Where(expression));
        return this;
    }

    public Query orderBy(OrderSpecifier... specifiers) {
        var orders = Arrays.stream(specifiers).map(specifier -> {
            if(specifier instanceof Column column) {
                return new Order(column, false);
            }
            return (Order) specifier;
        }).toList();
        commands.add(new OrderBy(orders));
        return this;
    }

    public Query limit(Integer limit) {
        commands.add(new Limit(limit));
        return this;
    }

    public Query offset(Integer offset) {
        commands.add(new Offset(offset));
        return this;
    }

    @Override
    public List<Command> build() {
        return commands;
    }
}