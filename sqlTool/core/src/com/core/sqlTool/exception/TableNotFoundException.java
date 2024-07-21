package com.core.sqlTool.exception;

import com.core.sqlTool.model.domain.Projection;
import com.core.sqlTool.model.domain.Table;

import java.util.stream.Collectors;

public class TableNotFoundException extends RuntimeException {

    public TableNotFoundException(String name, Projection projection) {

        super("Unable to find table with name '%s' in the context. There is(are) only: %s"
                .formatted(name, projection.tables().stream().map(Table::name).collect(Collectors.joining(", "))));

    }

}
