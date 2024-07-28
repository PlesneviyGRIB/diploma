package com.core.sqlTool.exception;

import com.core.sqlTool.model.expression.Expression;

public class ComputedTypeException extends RuntimeException {

    public ComputedTypeException(Expression expression) {

        super("Computed columnType of expression or part of expression '%s' is invalid".formatted(expression.stringify()));

    }

}
