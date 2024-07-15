package com.savchenko.sqlTool.exception;

import com.savchenko.sqlTool.model.domain.LazyTable;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;

import java.util.stream.Collectors;

import static java.lang.String.format;

public class TableNotFoundException extends RuntimeException {
    public TableNotFoundException(String name, Projection projection) {
        super(format("Unable to find table '%s' in context. There is(are) only [%s]", name, projection.tables().stream().map(Table::name).collect(Collectors.joining(", "))));
    }
}
