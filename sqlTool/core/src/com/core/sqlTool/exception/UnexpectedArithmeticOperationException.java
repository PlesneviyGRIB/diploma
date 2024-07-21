package com.core.sqlTool.exception;

import com.client.sqlTool.expression.Operator;
import com.core.sqlTool.model.expression.Value;

public class UnexpectedArithmeticOperationException extends RuntimeException {

    public UnexpectedArithmeticOperationException(Operator operator, Value<?> value1,  Value<?> value2) {

        super("".formatted());

    }

}
