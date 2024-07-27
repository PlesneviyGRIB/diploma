package com.client.sqlTool.expression;

import lombok.Getter;

@Getter
public abstract class Expression {

    private String expressionName;

    public Expression as(String name) {
        this.expressionName = name;
        return this;
    }

}
