package com.savchenko.sqlTool.query;

import com.savchenko.sqlTool.model.command.*;
import com.savchenko.sqlTool.model.command.domain.Command;
import com.savchenko.sqlTool.model.command.function.AggregationFunction;
import com.savchenko.sqlTool.model.command.join.*;
import com.savchenko.sqlTool.model.domain.Column;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.index.Index;
import org.apache.commons.lang3.builder.Builder;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Query implements Builder<List<Command>> {

    private final List<Command> commands = new LinkedList<>();

    public Query select(Column... columns) {
        commands.add(new Select(Arrays.stream(columns).toList()));
        return this;
    }

    public Query distinct() {
        commands.add(new Distinct());
        return this;
    }

    public Query from(String table) {
        commands.add(new From(table));
        return this;
    }

    public Query innerJoin(Query query, Expression expression, JoinStrategy strategy) {
        commands.add(new InnerJoin(query.build(), expression, strategy));
        return this;
    }

    public Query leftJoin(Query query, Expression expression, JoinStrategy strategy) {
        commands.add(new LeftJoin(query.build(), expression, strategy));
        return this;
    }

    public Query rightJoin(Query query, Expression expression, JoinStrategy strategy) {
        commands.add(new RightJoin(query.build(), expression, strategy));
        return this;
    }

    public Query fullJoin(Query query, Expression expression, JoinStrategy strategy) {
        commands.add(new FullJoin(query.build(), expression, strategy));
        return this;
    }

    public Query where(Expression expression) {
        commands.add(new Where(expression));
        return this;
    }

    public Query orderBy(Map<Column, Boolean> map) {
        var orders = map.entrySet().stream()
                .map(entry -> new Order(entry.getKey(), entry.getValue()))
                .toList();
        commands.add(new OrderBy(orders));
        return this;
    }

    public Query groupBy(Map<Column, AggregationFunction> map) {
        commands.add(new GroupBy(map));
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

    public Query constructIndex(Index index) {
        commands.add(new ConstructIndex(index));
        return this;
    }

    public Query as(String alias) {
        commands.add(new Alias(alias));
        return this;
    }

    @Override
    public List<Command> build() {
        return commands;
    }
}