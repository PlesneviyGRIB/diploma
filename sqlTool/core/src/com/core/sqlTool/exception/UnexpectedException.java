package com.core.sqlTool.exception;

import static java.lang.String.format;

public class UnexpectedException extends RuntimeException {

    public UnexpectedException() {
    }

    public UnexpectedException(String format, Object... objects) {
        super(format(format, objects));
    }
}
