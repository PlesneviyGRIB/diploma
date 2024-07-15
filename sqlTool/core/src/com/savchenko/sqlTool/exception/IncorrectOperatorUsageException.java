package com.savchenko.sqlTool.exception;

import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.operator.Operator;

import static java.lang.String.format;

public class IncorrectOperatorUsageException extends RuntimeException {
    public IncorrectOperatorUsageException(Operator operator, Expression expression) {
        super(format("Incorrect use of [%s]. Wrong operator arity in '%s'.", operator, expression.stringify()));
    }
}
