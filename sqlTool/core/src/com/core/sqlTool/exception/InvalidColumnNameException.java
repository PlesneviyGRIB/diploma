package com.core.sqlTool.exception;

import com.client.sqlTool.domain.Column;

public class InvalidColumnNameException extends RuntimeException {

    public InvalidColumnNameException(Column column) {

        super("Column name %s is invalid. Please, use dot as separator: table.column".formatted(column.getColumnName()));

    }

}
