package com.client.sqlTool.expression;

import lombok.Getter;

@Getter
public class String extends Expression {

    private final java.lang.String value;

    private String(java.lang.String value) {
        this.value = value;
    }

    public static String of(java.lang.String value) {
        return new String(value);
    }

}
