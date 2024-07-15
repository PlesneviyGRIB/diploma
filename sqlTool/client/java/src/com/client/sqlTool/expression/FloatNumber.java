package com.client.sqlTool.expression;

public class FloatNumber implements Expression {

    private final float value;

    private FloatNumber(float value) {
        this.value = value;
    }

    public static FloatNumber of(float value) {
        return new FloatNumber(value);
    }

}
