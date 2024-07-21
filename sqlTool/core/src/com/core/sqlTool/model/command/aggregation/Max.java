package com.core.sqlTool.model.command.aggregation;

import com.core.sqlTool.model.expression.Value;

import java.util.List;

public record Max() implements AggregationFunction {

    @Override
    public Value<?> aggregate(List<Value<?>> values) {
        var sortedValues = values.stream().sorted().toList();
        return sortedValues.get(sortedValues.size() - 1);
    }

}
