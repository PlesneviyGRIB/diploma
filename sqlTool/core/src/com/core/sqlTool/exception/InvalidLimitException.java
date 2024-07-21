package com.core.sqlTool.exception;

public class InvalidLimitException extends RuntimeException {

    public InvalidLimitException(Integer limit) {
        super("Limit can not be less than 0! Current value is '%s'".formatted(limit));
    }

}