package com.client.sqlTool.expression;

import lombok.Getter;

@Getter
public class List extends Expression {

    private final java.util.List<Expression> expressions;

    private List(java.util.List<Expression> expressions) {
        this.expressions = expressions;
    }

    public static List of(Expression... expressions) {
        return new List(java.util.List.of(expressions));
    }

}