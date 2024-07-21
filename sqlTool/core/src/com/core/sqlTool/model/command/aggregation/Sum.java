package com.core.sqlTool.model.command.aggregation;

import com.client.sqlTool.expression.Operator;
import com.core.sqlTool.model.expression.Value;

import java.util.List;

public record Sum() implements AggregationFunction {

    @Override
    public Value<?> aggregate(List<Value<?>> values) {

        var rawTypeValues = (List<Value>) (Object) values;
        var valuesSplice = rawTypeValues.subList(1, rawTypeValues.size());

        var sum = rawTypeValues.get(0);

        for (var value : valuesSplice) {
            sum = sum.processArithmetic(Operator.PLUS, value);
        }

        return sum;
    }

}
