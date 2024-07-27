package com.client.sqlTool.expression;

import lombok.Getter;

@Getter
public class Binary extends Expression {

    private final Operator operator;

    private final Expression left;

    private final Expression right;

    private Binary(Operator operator, Expression left, Expression right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public static Binary of(Operator operator, Expression left, Expression right) {
        return new Binary(operator, left, right);
    }

}
