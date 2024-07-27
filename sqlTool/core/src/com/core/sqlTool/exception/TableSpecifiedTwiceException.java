package com.core.sqlTool.exception;

public class TableSpecifiedTwiceException extends RuntimeException {

    public TableSpecifiedTwiceException(String tableName) {

        super("Table with name '%s' specified more than once".formatted(tableName));

    }

}
