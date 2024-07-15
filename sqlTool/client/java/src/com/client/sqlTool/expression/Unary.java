package com.client.sqlTool.expression;

public class Unary implements Expression {

    private final Operator operator;

    private final Expression expression;

    private Unary(Operator operator, Expression expression) {
        this.operator = operator;
        this.expression = expression;
    }

    public static Unary of(Operator operator, Expression expression) {
        return new Unary(operator, expression);
    }

}
