package com.core.sqlTool.exception;

public class UnsupportedTypeException extends RuntimeException {

    public UnsupportedTypeException(Class<?> clazz) {

        super("Type '%s' not supported by the tool".formatted(clazz.getSimpleName()));

    }

}
