package com.core.sqlTool.model.command.aggregation;

import com.client.sqlTool.expression.Operator;
import com.core.sqlTool.exception.UnexpectedException;
import com.core.sqlTool.model.expression.FloatNumberValue;
import com.core.sqlTool.model.expression.NumberValue;
import com.core.sqlTool.model.expression.Value;

import java.util.List;

public record Average() implements AggregationFunction {

    @Override
    public Value<?> aggregate(List<Value<?>> values) {

        var sum = getFloatNumberValue(new Sum().aggregate(values));
        var count = getFloatNumberValue(new Count().aggregate(values));

        return sum.processArithmetic(Operator.DIVISION, count);
    }

    private FloatNumberValue getFloatNumberValue(Value<?> value) {
        if (value instanceof FloatNumberValue floatNumberValue) {
            return floatNumberValue;
        }

        if (value instanceof NumberValue numberValue) {
            return new FloatNumberValue(numberValue.value().floatValue());
        }

        throw new UnexpectedException();
    }

}
