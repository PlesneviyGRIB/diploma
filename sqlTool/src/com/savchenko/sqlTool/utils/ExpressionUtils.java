package com.savchenko.sqlTool.utils;

import com.savchenko.sqlTool.exception.ColumnNotFoundException;
import com.savchenko.sqlTool.model.domain.Column;
import com.savchenko.sqlTool.model.domain.ExternalRow;
import com.savchenko.sqlTool.model.domain.Row;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.expression.NullValue;
import com.savchenko.sqlTool.model.visitor.ExpressionTraversal;
import com.savchenko.sqlTool.model.visitor.ExpressionValidator;

import java.util.List;

public class ExpressionUtils {
    public static boolean columnsContainsNulls(Row row, ExternalRow externalRow, Expression expression) {
        var o = new Object() {
            public boolean nullPresents;
        };

        expression.accept(new ExpressionTraversal() {
            @Override
            public Void visit(Column column) {
                var value = row.getValue(column).or(() -> externalRow.getValue(column)).orElse(null);
                if (value instanceof NullValue) {
                    o.nullPresents = true;
                }
                return null;
            }
        });

        return o.nullPresents;
    }

    public static boolean relatedToTable(List<Column> columns, Expression expression, ExternalRow externalRow) {
        try {
            expression.accept(new ExpressionValidator(columns, externalRow));
            return true;
        } catch (ColumnNotFoundException e) {
            return false;
        }
    }

}
