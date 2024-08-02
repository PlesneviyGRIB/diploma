package com.core.sqlTool.exception;

import com.core.sqlTool.model.domain.Column;
import com.core.sqlTool.model.expression.Expression;

import java.util.List;
import java.util.stream.Collectors;

public class MoreThanOneColumnInSubQueryException extends RuntimeException {

    public MoreThanOneColumnInSubQueryException(List<Column> columns) {

        super("Expected exactly one columnName, but found: %s"
                .formatted(columns.stream().map(Expression::stringify).collect(Collectors.joining(", "))));

    }

}
