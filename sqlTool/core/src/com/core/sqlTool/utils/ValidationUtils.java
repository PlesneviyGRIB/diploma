package com.core.sqlTool.utils;

import com.core.sqlTool.exception.UnexpectedException;
import com.core.sqlTool.model.domain.Column;
import com.core.sqlTool.model.domain.ExternalHeaderRow;
import com.core.sqlTool.model.expression.Expression;
import com.core.sqlTool.model.visitor.ExpressionValidator;

import java.util.List;

public class ValidationUtils {

    public static void expectBooleanValueAsResolvedType(Expression expression, List<Column> columns, ExternalHeaderRow externalRow) {
        var resolvedClass = expression.accept(new ExpressionValidator(columns, externalRow));
        var tokens = resolvedClass.getTypeName().split("\\.");
        var type = tokens[tokens.length - 1];
        if (!type.equals("BooleanValue")) {
            throw new UnexpectedException("Expected BooleanValue here, but found %s", type);
        }
    }

}
