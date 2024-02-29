package com.savchenko.sqlTool.exception;

import com.savchenko.sqlTool.model.expression.Expression;

public class ComputedTypeException extends ValidationException {
    public ComputedTypeException(Expression expression) {
        super("Computed type of expression or part of expression '%s' is invalid", expression.stringify());
    }
}
