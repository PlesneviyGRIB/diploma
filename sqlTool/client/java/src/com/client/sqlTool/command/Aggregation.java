package com.client.sqlTool.command;

import com.client.sqlTool.domain.AggregationType;
import com.client.sqlTool.expression.Expression;
import lombok.Getter;

@Getter
public class Aggregation {

    private final Expression expression;

    private final AggregationType aggregationType;

    private Aggregation(Expression expression, AggregationType aggregationType) {
        this.expression = expression;
        this.aggregationType = aggregationType;
    }

    public static Aggregation max(Expression expression) {
        return new Aggregation(expression, AggregationType.MAX);
    }

    public static Aggregation min(Expression expression) {
        return new Aggregation(expression, AggregationType.MIN);
    }

    public static Aggregation average(Expression expression) {
        return new Aggregation(expression, AggregationType.AVERAGE);
    }

    public static Aggregation count(Expression expression) {
        return new Aggregation(expression, AggregationType.COUNT);
    }

    public static Aggregation sum(Expression expression) {
        return new Aggregation(expression, AggregationType.SUM);
    }

}
