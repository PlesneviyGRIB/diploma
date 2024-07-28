package com.core.sqlTool.utils;

import com.core.sqlTool.exception.AmbiguousColumnReferenceException;
import com.core.sqlTool.exception.UnexpectedException;
import com.core.sqlTool.model.domain.Column;
import com.core.sqlTool.model.domain.ExternalHeaderRow;
import com.core.sqlTool.model.expression.Expression;
import com.core.sqlTool.model.visitor.ExpressionResultTypeResolver;
import org.apache.commons.collections4.ListUtils;

import java.util.List;

public class ValidationUtils {

    public static void expectBooleanValueAsResolvedType(Expression expression, List<Column> columns, ExternalHeaderRow externalRow) {
        var resolvedClass = expression.accept(new ExpressionResultTypeResolver(columns, externalRow));
        var tokens = resolvedClass.getTypeName().split("\\.");
        var type = tokens[tokens.length - 1];
        if (!type.equals("BooleanValue")) {
            throw new UnexpectedException("Expected BooleanValue, but found %s", type);
        }
    }

    public static void assertDifferentColumns(List<Column> columns1, List<Column> columns2) {
        var columnIntersection = ListUtils.intersection(columns1, columns2);
        if (!columnIntersection.isEmpty()) {
            throw new AmbiguousColumnReferenceException(columnIntersection.get(0), ListUtils.union(columns1, columns2));
        }
    }

}
