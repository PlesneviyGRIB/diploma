package com.savchenko.sqlTool.model.command.function;

import com.savchenko.sqlTool.model.expression.ExpressionList;
import com.savchenko.sqlTool.model.expression.Value;

public class Min implements AggregationFunction {
    @Override
    public Value<?> apply(ExpressionList list) {
        return null;
    }

}