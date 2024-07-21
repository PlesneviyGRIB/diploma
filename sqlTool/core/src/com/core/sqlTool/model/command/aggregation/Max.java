package com.core.sqlTool.model.command.aggregation;

import com.core.sqlTool.model.expression.ExpressionList;
import com.core.sqlTool.model.expression.Value;

public record Max() implements AggregationFunction {

    @Override
    public Value<?> aggregate(ExpressionList list) {
        return null;
    }

}
