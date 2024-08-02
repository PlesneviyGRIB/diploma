package com.core.sqlTool.utils;

import com.core.sqlTool.model.domain.Column;
import com.core.sqlTool.model.domain.ExternalHeaderRow;
import com.core.sqlTool.model.domain.HeaderRow;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.expression.Expression;
import com.core.sqlTool.model.expression.NullValue;
import com.core.sqlTool.model.expression.Value;
import com.core.sqlTool.model.resolver.Resolver;
import com.core.sqlTool.model.visitor.ContextSensitiveExpressionQualifier;
import com.core.sqlTool.model.visitor.ExpressionCalculator;
import com.core.sqlTool.model.visitor.ExpressionTraversal;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ExpressionUtils {

    public static boolean columnsContainsNulls(HeaderRow HeaderRow, ExternalHeaderRow externalRow, Expression expression) {
        var o = new Object() {
            public boolean nullPresents;
        };

        expression.accept(new ExpressionTraversal() {
            @Override
            public Void visit(Column column) {
                var value = HeaderRow.getValue(column).or(() -> externalRow.getValue(column)).orElse(null);
                if (value instanceof NullValue) {
                    o.nullPresents = true;
                }
                return null;
            }
        });

        return o.nullPresents;
    }

    public static Map<Expression, Value<?>> calculateContextInsensitiveExpressions(List<Expression> expressions, LazyTable lazyTable, Resolver resolver) {
        return expressions.stream()
                .map(expression -> {

                    var isContextSensitiveExpression = expression.accept(new ContextSensitiveExpressionQualifier(lazyTable.columns()));

                    if (isContextSensitiveExpression) {
                        return null;
                    }

                    var value = expression.accept(new ExpressionCalculator(resolver, HeaderRow.empty(), lazyTable.externalRow()));

                    return Pair.<Expression, Value<?>>of(expression, value);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight, (v1, v2) -> v2));
    }

    public static Value<?> calculateExpression(Expression expression, HeaderRow headerRow, ExternalHeaderRow externalRow, Resolver resolver, Map<Expression, Value<?>> calculatedValueByExpressionMap) {
        var value = calculatedValueByExpressionMap.get(expression);
        if (value != null) {
            return value;
        }
        return expression.accept(new ExpressionCalculator(resolver, headerRow, externalRow));
    }

}
