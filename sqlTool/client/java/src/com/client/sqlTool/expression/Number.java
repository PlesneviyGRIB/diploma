package com.client.sqlTool.expression;

public class Number implements Expression {

    private final Integer value;

    private Number(Integer value) {
        this.value = value;
    }

    public static Number of(Integer value) {
        return new Number(value);
    }

}
