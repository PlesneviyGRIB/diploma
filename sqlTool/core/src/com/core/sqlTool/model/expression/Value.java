package com.core.sqlTool.model.expression;

import com.client.sqlTool.expression.Operator;
import com.core.sqlTool.exception.UnexpectedArithmeticOperationException;


public sealed interface Value<O> extends Expression, Comparable<O> permits BooleanValue, FloatNumberValue, NullValue, NumberValue, StringValue, TimestampValue {

    default Value<O> processArithmetic(Operator operator, Value<O> operand) {
        throw new UnexpectedArithmeticOperationException(operator, this, operand);
    }

}
