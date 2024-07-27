package com.core.sqlTool.exception;

public class InvalidColumnNameException extends RuntimeException {

    public InvalidColumnNameException(String columnFullName) {

        super("Column name %s is invalid. Please, use dot as separator: table.column".formatted(columnFullName));

    }

}
