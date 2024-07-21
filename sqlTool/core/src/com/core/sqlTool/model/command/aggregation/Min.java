package com.core.sqlTool.model.command.aggregation;

import com.core.sqlTool.model.expression.Value;

import java.util.List;

public record Min() implements AggregationFunction {

    @Override
    public Value<?> aggregate(List<Value<?>> values) {
        var sortedValues = values.stream().sorted().toList();
        return sortedValues.get(0);
    }

}
