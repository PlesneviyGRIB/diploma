package com.core.sqlTool.exception;

import com.core.sqlTool.model.domain.Column;
import com.core.sqlTool.model.expression.Expression;

import java.util.List;
import java.util.stream.Collectors;

public class AmbiguousColumnReferenceException extends RuntimeException {

    public AmbiguousColumnReferenceException(Column column, List<Column> columns) {

        super("Ambiguous column reference %s, unable to detect actual column. Columns from context: %s"
                .formatted(column.stringify(), columns.stream().map(Expression::stringify).collect(Collectors.joining(", "))));

    }

}
