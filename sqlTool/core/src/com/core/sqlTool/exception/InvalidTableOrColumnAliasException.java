package com.core.sqlTool.exception;

public class InvalidTableOrColumnAliasException extends RuntimeException {

    public InvalidTableOrColumnAliasException(String alias) {

        super("Invalid table or column alias '%s'".formatted(alias));

    }
}
