package com.savchenko.sqlTool.exception;

import com.savchenko.sqlTool.model.domain.Column;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class ColumnNotFoundException extends RuntimeException {
    public ColumnNotFoundException(Column column, List<Column> columns) {
        super(format("Unable to find column '%s' in context. There is(are) only [%s]", column, columns.stream().map(Objects::toString).collect(Collectors.joining(", "))));
    }
}
