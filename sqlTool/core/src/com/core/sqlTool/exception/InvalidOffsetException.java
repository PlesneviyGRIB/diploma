package com.core.sqlTool.exception;

public class InvalidOffsetException extends RuntimeException {

    public InvalidOffsetException(Integer offset) {
        super("Offset can not be less than 0! Current value is '%s'".formatted(offset));
    }

}
