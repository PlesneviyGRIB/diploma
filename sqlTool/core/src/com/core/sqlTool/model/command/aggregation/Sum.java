package com.core.sqlTool.model.command.aggregation;

import com.core.sqlTool.model.expression.ValueList;
import com.core.sqlTool.model.expression.Value;

public record Sum() implements AggregationFunction {

    @Override
    public Value<?> aggregate(ValueList list) {
        return null;
    }

}
