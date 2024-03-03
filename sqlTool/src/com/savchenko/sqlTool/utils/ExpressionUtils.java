package com.savchenko.sqlTool.utils;

import com.savchenko.sqlTool.exception.ColumnNotFoundException;
import com.savchenko.sqlTool.model.domain.Column;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.expression.NullValue;
import com.savchenko.sqlTool.model.expression.Value;
import com.savchenko.sqlTool.model.visitor.ExpressionTraversal;
import com.savchenko.sqlTool.model.visitor.ExpressionValidator;

import java.util.List;
import java.util.Map;

public class ExpressionUtils {
    public static boolean columnsContainsNulls(Map<Column, Value<?>> columnValueMap, Expression expression) {
        var o = new Object() {
            public boolean nullPresents;
        };

        expression.accept(new ExpressionTraversal() {
            @Override
            public Void visit(Column column) {
                var value = columnValueMap.get(column);
                if (value instanceof NullValue) {
                    o.nullPresents = true;
                }
                return null;
            }
        });

        return o.nullPresents;
    }

    public static boolean relatedToTable(List<Column> columns, Expression expression) {
        try {
            expression.accept(new ExpressionValidator(columns));
            return true;
        } catch (ColumnNotFoundException e) {
            return false;
        }
    }

}
