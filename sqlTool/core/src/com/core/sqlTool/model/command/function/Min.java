package com.core.sqlTool.model.command.function;

import com.core.sqlTool.model.expression.ExpressionList;
import com.core.sqlTool.model.expression.Value;

public record Min() implements AggregationFunction {

    @Override
    public Value<?> aggregate(ExpressionList list) {
        return null;
    }

}
