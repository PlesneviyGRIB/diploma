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

    public static Aggregation of(Expression expression, AggregationType aggregationType) {
        return new Aggregation(expression, aggregationType);
    }

}
