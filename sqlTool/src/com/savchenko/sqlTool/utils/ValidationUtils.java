package com.savchenko.sqlTool.utils;

import com.savchenko.sqlTool.exception.UnexpectedException;
import com.savchenko.sqlTool.model.domain.Column;
import com.savchenko.sqlTool.model.domain.ExternalHeaderRow;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.visitor.ExpressionValidator;

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
