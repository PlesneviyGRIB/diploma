package com.savchenko.sqlTool.model.expression;

import com.savchenko.sqlTool.model.operator.Operator;

public record UnaryOperation(Operator operator, Expression expression) implements Expression {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}
