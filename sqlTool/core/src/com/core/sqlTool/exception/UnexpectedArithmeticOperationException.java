package com.core.sqlTool.exception;

import com.client.sqlTool.expression.Operator;
import com.core.sqlTool.model.expression.Value;

public class UnexpectedArithmeticOperationException extends RuntimeException {

    public UnexpectedArithmeticOperationException(Operator operator, Value<?> value1,  Value<?> value2) {

        super("Arithmetic operation '%s %s %s' is invalid".formatted(value1.stringify(), operator.getDesignator(), value2.stringify()));

    }

}
