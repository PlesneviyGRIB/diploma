package com.client.sqlTool.expression;

import lombok.Getter;

@Getter
public class Str extends Expression {

    private final String value;

    private Str(String value) {
        this.value = value;
    }

    public static Str of(String value) {
        return new Str(value);
    }

}
