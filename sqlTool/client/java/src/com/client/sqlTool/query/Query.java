package com.client.sqlTool.query;

import com.client.sqlTool.command.*;
import com.client.sqlTool.domain.Column;
import com.client.sqlTool.domain.JoinStrategy;
import com.client.sqlTool.domain.JoinType;
import com.client.sqlTool.expression.Expression;
import lombok.Getter;
import org.apache.commons.collections4.ListUtils;

import java.util.LinkedList;
import java.util.List;

@Getter
public class Query extends Expression {

    private final List<Command> commands = new LinkedList<>();

    private Query(From from) {
        commands.add(from);
    }

    public static Query from(String table) {
        return new Query(new From(table));
    }

    public Query select(Expression expression, Expression... expressions) {
        commands.add(new Select(union(expression, expressions)));
        return this;
    }

    public Query distinct() {
        commands.add(new Distinct());
        return this;
    }

    public Query leftLoopJoin(Query query, Expression expression) {
        commands.add(new Join(JoinType.LEFT, query.commands, expression, JoinStrategy.LOOP));
        return this;
    }

    public Query leftMergeJoin(Query query, Expression expression) {
        commands.add(new Join(JoinType.LEFT, query.commands, expression, JoinStrategy.MERGE));
        return this;
    }

    public Query leftHashJoin(Query query, Expression expression) {
        commands.add(new Join(JoinType.LEFT, query.commands, expression, JoinStrategy.HASH));
        return this;
    }

    public Query rightLoopJoin(Query query, Expression expression) {
        commands.add(new Join(JoinType.RIGHT, query.commands, expression, JoinStrategy.LOOP));
        return this;
    }

    public Query rightMergeJoin(Query query, Expression expression) {
        commands.add(new Join(JoinType.RIGHT, query.commands, expression, JoinStrategy.MERGE));
        return this;
    }

    public Query rightHashJoin(Query query, Expression expression) {
        commands.add(new Join(JoinType.RIGHT, query.commands, expression, JoinStrategy.HASH));
        return this;
    }

    public Query innerLoopJoin(Query query, Expression expression) {
        commands.add(new Join(JoinType.INNER, query.commands, expression, JoinStrategy.LOOP));
        return this;
    }

    public Query innerMergeJoin(Query query, Expression expression) {
        commands.add(new Join(JoinType.INNER, query.commands, expression, JoinStrategy.MERGE));
        return this;
    }

    public Query innerHashJoin(Query query, Expression expression) {
        commands.add(new Join(JoinType.INNER, query.commands, expression, JoinStrategy.HASH));
        return this;
    }

    public Query fullLoopJoin(Query query, Expression expression) {
        commands.add(new Join(JoinType.FULL, query.commands, expression, JoinStrategy.LOOP));
        return this;
    }

    public Query fullMergeJoin(Query query, Expression expression) {
        commands.add(new Join(JoinType.FULL, query.commands, expression, JoinStrategy.MERGE));
        return this;
    }

    public Query fullHashJoin(Query query, Expression expression) {
        commands.add(new Join(JoinType.FULL, query.commands, expression, JoinStrategy.HASH));
        return this;
    }

    public Query where(Expression expression) {
        commands.add(new Where(expression));
        return this;
    }

    public Query orderBy(Order order, Order... orders) {
        commands.add(new OrderBy(union(order, orders)));
        return this;
    }

    public GroupByAggregation groupBy(Expression expression, Expression... expressions) {
        var groupBy = new GroupBy(union(expression, expressions), List.of());
        return new GroupByAggregation(this, groupBy);
    }

    public Query limit(Integer limit) {
        commands.add(new Limit(limit));
        return this;
    }

    public Query offset(Integer offset) {
        commands.add(new Offset(offset));
        return this;
    }

    public Query constructIndex(Index index, Column column, Column... columns) {
        commands.add(new ConstructIndex(index, union(column, columns)));
        return this;
    }

    public Query as(String alias) {
        commands.add(new Alias(alias));
        return this;
    }

    private <T> List<T> union(T mandatoryParam, T... optionalParams) {
        return ListUtils.union(List.of(mandatoryParam), List.of(optionalParams));
    }

    public static class GroupByAggregation {

        private final Query query;

        private final GroupBy groupBy;

        private GroupByAggregation(Query query, GroupBy groupBy) {
            this.query = query;
            this.groupBy = groupBy;
        }

        public Query aggregate(Aggregation... aggregations) {
            query.commands.add(new GroupBy(groupBy.expressions(), List.of(aggregations)));
            return query;
        }

    }

}
