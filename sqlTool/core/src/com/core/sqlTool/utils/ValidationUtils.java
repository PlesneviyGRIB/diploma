package com.core.sqlTool.utils;

import com.core.sqlTool.exception.AmbiguousColumnReferenceException;
import com.core.sqlTool.exception.NonBooleanResultTypeException;
import com.core.sqlTool.model.domain.Column;
import com.core.sqlTool.model.domain.ExternalHeaderRow;
import com.core.sqlTool.model.expression.BooleanValue;
import com.core.sqlTool.model.expression.Expression;
import com.core.sqlTool.model.visitor.ExpressionResultTypeResolver;
import org.apache.commons.collections4.ListUtils;

import java.util.List;

public class ValidationUtils {

    public static void expectBooleanValueAsResolvedType(Expression expression, List<Column> columns, ExternalHeaderRow externalRow) {

        var resolvedClass = expression.accept(new ExpressionResultTypeResolver(columns, externalRow));

        if (!resolvedClass.equals(BooleanValue.class)) {
            throw new NonBooleanResultTypeException(expression, resolvedClass);
        }

    }

    public static void assertDifferentColumns(List<Column> columns1, List<Column> columns2) {

        var columnIntersection = ListUtils.intersection(columns1, columns2);

        if (!columnIntersection.isEmpty()) {
            throw new AmbiguousColumnReferenceException(columnIntersection.get(0), ListUtils.union(columns1, columns2));
        }

    }

}
