package com.core.sqlTool.exception;

import com.core.sqlTool.model.domain.Column;
import com.core.sqlTool.model.expression.Expression;

import java.util.List;
import java.util.stream.Collectors;

public class ColumnNotFoundException extends RuntimeException {

    public ColumnNotFoundException(Column column, List<Column> availableColumns) {

        super("Unable to find column with name '%s' in the context. There is(are) only: %s"
                .formatted(column, availableColumns.stream().map(Expression::stringify).collect(Collectors.joining(", "))));

    }

}
