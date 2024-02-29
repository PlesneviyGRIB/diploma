package com.savchenko.sqlTool.query;

import com.savchenko.sqlTool.model.Builder;
import com.savchenko.sqlTool.model.command.*;
import com.savchenko.sqlTool.model.expression.BinaryOperation;
import com.savchenko.sqlTool.model.expression.BooleanValue;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.index.Index;
import com.savchenko.sqlTool.model.operator.Operator;
import com.savchenko.sqlTool.model.structure.Column;
import com.savchenko.sqlTool.repository.Projection;
import org.apache.commons.collections4.ListUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Query implements Builder<List<Command>> {
    private final Projection projection;
    private final List<Command> commands = new LinkedList<>();

    public Query(Projection projection) {
        this.projection = projection;
    }

    public Query select(Column column, Column... columns) {
        var targetColumns = ListUtils.union(List.of(column), Arrays.asList(columns));
        commands.add(new Select(targetColumns, projection));
        return this;
    }

    public Query from(String table) {
        commands.add(new From(table, projection));
        return this;
    }

    public Query innerJoin(Query query, Expression expression, JoinStrategy strategy) {
        commands.add(new InnerJoin(query.build(), expression, strategy, projection));
        return this;
    }

    public Query leftJoin(Query query, Expression expression, JoinStrategy strategy) {
        commands.add(new LeftJoin(query.build(), expression, strategy, projection));
        return this;
    }

    public Query rightJoin(Query query, Expression expression, JoinStrategy strategy) {
        commands.add(new RightJoin(query.build(), expression, strategy, projection));
        return this;
    }

    public Query fullJoin(Query query, Expression expression, JoinStrategy strategy) {
        commands.add(new FullJoin(query.build(), expression, strategy, projection));
        return this;
    }

    public Query where(Expression... expressions) {
        var expression = Arrays.stream(expressions).reduce(new BooleanValue(true), (p, c) -> new BinaryOperation(Operator.AND, p, c));
        commands.add(new Where(expression, projection));
        return this;
    }

    public Query orderBy(OrderSpecifier... specifiers) {
        var orders = Arrays.stream(specifiers).map(specifier -> {
            if(specifier instanceof Column column) {
                return new Order(column, false);
            }
            return (Order) specifier;
        }).toList();
        commands.add(new OrderBy(orders, projection));
        return this;
    }

    public Query limit(Integer limit) {
        commands.add(new Limit(limit, projection));
        return this;
    }

    public Query offset(Integer offset) {
        commands.add(new Offset(offset, projection));
        return this;
    }

    public Query constructIndex(Index index) {
        commands.add(new ConstructIndex(index, projection));
        return this;
    }

    public Query as(String alias) {
        commands.add(new Alias(alias, projection));
        return this;
    }

    @Override
    public List<Command> build() {
        return commands;
    }
}