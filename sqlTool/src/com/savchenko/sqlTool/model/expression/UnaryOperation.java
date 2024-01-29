package com.savchenko.sqlTool.model.expression;

import com.savchenko.sqlTool.model.operator.Operator;

public record UnaryOperation(Operator operator, Expression<?> expression) implements Expression<UnaryOperation> {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int compareTo(UnaryOperation unaryOperation) {
        return 0;
    }
}
