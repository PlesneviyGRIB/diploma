package com.core.sqlTool.exception;

import com.client.sqlTool.expression.Operator;
import com.core.sqlTool.model.expression.Expression;

public class IncorrectOperatorUsageException extends RuntimeException {

    public IncorrectOperatorUsageException(Operator operator, Expression expression) {

        super("Incorrect use of '%s'. Wrong operator arity in '%s'".formatted(operator, expression.stringify()));

    }

}
