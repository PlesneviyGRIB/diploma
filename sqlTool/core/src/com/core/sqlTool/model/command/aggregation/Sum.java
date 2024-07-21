package com.core.sqlTool.model.command.aggregation;

import com.core.sqlTool.model.expression.Value;

import java.util.List;

public record Sum() implements AggregationFunction {

    @Override
    public Value<?> aggregate(List<Value<?>> values) {
        return null;
    }

}
