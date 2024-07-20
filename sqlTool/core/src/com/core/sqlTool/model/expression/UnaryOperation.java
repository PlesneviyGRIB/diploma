package com.core.sqlTool.model.expression;

import com.client.sqlTool.expression.Operator;

public record UnaryOperation(Operator operator, Expression expression) implements Expression {

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}
