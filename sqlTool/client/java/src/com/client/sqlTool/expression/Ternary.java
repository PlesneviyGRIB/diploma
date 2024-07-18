package com.client.sqlTool.expression;

import lombok.Getter;

@Getter
public class Ternary implements Expression {

    private final Operator operator;

    private final Expression first;

    private final Expression second;

    private final Expression third;

    private Ternary(Operator operator, Expression first, Expression second, Expression third) {
        this.operator = operator;
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public static Ternary of(Operator operator, Expression first, Expression second, Expression third) {
        return new Ternary(operator, first, second, third);
    }

}