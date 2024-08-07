package com.core.sqlTool.exception;

import com.core.sqlTool.model.expression.Expression;
import com.core.sqlTool.model.expression.Value;
import com.core.sqlTool.utils.PrinterUtils;

public class NonBooleanResultTypeException extends RuntimeException {

    public NonBooleanResultTypeException(Expression expression, Class<? extends Value<?>> valueClass) {

        super("Expected Boolean as resoled type for expression '%s', but found %s".formatted(expression.stringify(), PrinterUtils.valueToString(valueClass)));

    }
}
