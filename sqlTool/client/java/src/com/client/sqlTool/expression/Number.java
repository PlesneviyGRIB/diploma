package com.client.sqlTool.expression;

import lombok.Getter;

@Getter
public class Number extends Expression {

    private final Integer value;

    private Number(Integer value) {
        this.value = value;
    }

    public static Number of(Integer value) {
        return new Number(value);
    }

}
