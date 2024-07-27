package com.core.sqlTool.exception;

import com.core.sqlTool.model.expression.Expression;

public class UnnamedExpressionException extends RuntimeException {

    public UnnamedExpressionException(Expression expression) {

        super("Unnamed expression '%s' found. Please name expression to be able to reference the corresponding column".formatted(expression.stringify()));

    }

}
