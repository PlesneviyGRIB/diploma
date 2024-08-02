package com.core.sqlTool.exception;

import com.core.sqlTool.model.expression.Expression;

public class UnexpectedExpressionException extends RuntimeException {

    public UnexpectedExpressionException(Expression expression) {

        super("Expression '%s' used in wrong context".formatted(expression.stringify()));

    }

}
