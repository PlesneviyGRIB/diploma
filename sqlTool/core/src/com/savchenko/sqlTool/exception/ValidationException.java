package com.savchenko.sqlTool.exception;

import static java.lang.String.format;

public class ValidationException extends RuntimeException {
    public ValidationException(String format, Object... objects) {
        super(format(format, objects));
    }
}
