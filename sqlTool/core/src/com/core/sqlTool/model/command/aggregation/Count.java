package com.core.sqlTool.model.command.aggregation;

import com.core.sqlTool.model.expression.NumberValue;
import com.core.sqlTool.model.expression.Value;

import java.util.List;

public record Count() implements AggregationFunction {

    @Override
    public Value<?> aggregate(List<Value<?>> values) {
        return new NumberValue(values.size());
    }

}
