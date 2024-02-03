package com.savchenko.sqlTool.exception;

public class ComputedTypeException extends ValidationException {
    public ComputedTypeException() {
        super("Computed type of expression or part of expression is invalid");
    }
}
