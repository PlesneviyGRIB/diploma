package com.client.sqlTool.expression;

public class Str implements Expression {

    private final String value;

    private Str(String value) {
        this.value = value;
    }

    public static Str of(String value) {
        return new Str(value);
    }

}
