package com.client.sqlTool.expression;

import lombok.Getter;

@Getter
public class FloatNumber extends Expression {

    private final float value;

    private FloatNumber(float value) {
        this.value = value;
    }

    public static FloatNumber of(float value) {
        return new FloatNumber(value);
    }

}
