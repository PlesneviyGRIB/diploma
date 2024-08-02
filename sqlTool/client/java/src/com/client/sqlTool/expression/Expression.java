package com.client.sqlTool.expression;

import lombok.Getter;

@Getter
public abstract class Expression {

    private java.lang.String expressionName;

    public Expression as(java.lang.String name) {
        this.expressionName = name;
        return this;
    }

}
