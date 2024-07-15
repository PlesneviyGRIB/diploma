package com.core.sqlTool.model.expression;

import com.client.sqlTool.expression.Operator;
import com.core.sqlTool.exception.UnexpectedException;


public interface Value<O> extends Expression, Comparable<O> {

    default Value<O> processArithmetic(Operator operator, Value<O> operand) {
        throw new UnexpectedException();
    }

}
