package com.savchenko.sqlTool.exception;

import static java.lang.String.format;

public class ParseException extends RuntimeException {
    public ParseException(String format, Object... objects) {
        super(format(format, objects));
    }
}
