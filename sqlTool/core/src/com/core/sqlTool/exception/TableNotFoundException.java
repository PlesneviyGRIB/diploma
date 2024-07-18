package com.core.sqlTool.exception;

import com.core.sqlTool.model.domain.Projection;
import com.core.sqlTool.model.domain.Table;

import java.util.stream.Collectors;

import static java.lang.String.format;

public class TableNotFoundException extends RuntimeException {
    public TableNotFoundException(String name, Projection projection) {
        super(format("Unable to find tableName '%s' in context. There is(are) only [%s]", name, projection.tables().stream().map(Table::name).collect(Collectors.joining(", "))));
    }
}
