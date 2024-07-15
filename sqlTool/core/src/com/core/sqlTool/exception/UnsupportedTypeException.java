package com.core.sqlTool.exception;

import static java.lang.String.format;

public class UnsupportedTypeException extends RuntimeException {
    public UnsupportedTypeException() {
    }

    public UnsupportedTypeException(String format, Object... objects) {
        super(format(format, objects));
    }
}
