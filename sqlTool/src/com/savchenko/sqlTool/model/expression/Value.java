package com.savchenko.sqlTool.model.expression;

import com.savchenko.sqlTool.exception.UnexpectedException;
import com.savchenko.sqlTool.model.operator.Operator;

public interface Value <O> extends Expression<O> {
    default Value<O> processArithmetic(Operator operator, Value<O> operand) {
        throw new UnexpectedException();
    }
}
