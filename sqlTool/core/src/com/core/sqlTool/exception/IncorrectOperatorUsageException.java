package com.core.sqlTool.exception;

import com.client.sqlTool.expression.Operator;
import com.core.sqlTool.model.expression.Expression;

import static java.lang.String.format;

public class IncorrectOperatorUsageException extends RuntimeException {

    public IncorrectOperatorUsageException(Operator operator, Expression expression) {
        super(format("Incorrect use of [%s]. Wrong operator arity in '%s'.", operator, expression.stringify()));
    }

}
