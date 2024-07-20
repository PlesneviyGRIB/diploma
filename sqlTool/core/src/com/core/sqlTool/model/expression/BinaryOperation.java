package com.core.sqlTool.model.expression;

import com.client.sqlTool.expression.Operator;

public record BinaryOperation(Operator operator, Expression left, Expression right) implements Expression {

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}
