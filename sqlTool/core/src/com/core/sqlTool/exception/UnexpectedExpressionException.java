package com.core.sqlTool.exception;

import com.core.sqlTool.model.expression.Expression;

public class UnexpectedExpressionException extends UnexpectedException {
    public UnexpectedExpressionException(Expression expression) {
        super("Wrong expression '%s' found. Maybe expression used in wrong context", expression.stringify());
    }
}
