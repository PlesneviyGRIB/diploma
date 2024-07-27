package com.client.sqlTool.expression;

import lombok.Getter;

@Getter
public class Bool extends Expression {

    private final boolean value;

    private Bool(boolean value) {
        this.value = value;
    }

    public static final Bool TRUE = new Bool(true);

    public static final Bool FALSE = new Bool(false);
}
