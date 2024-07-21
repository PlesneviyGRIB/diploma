package com.core.sqlTool.model.command.aggregation;

import com.client.sqlTool.expression.Operator;
import com.core.sqlTool.model.expression.Value;

import java.util.List;

public record Average() implements AggregationFunction {

    @Override
    public Value<?> aggregate(List<Value<?>> values) {

        var sum = new Sum().aggregate(values);
        var count = new Count().aggregate(values);

        return sum.processArithmetic(Operator.DIVISION, (Value) count);
    }

}
