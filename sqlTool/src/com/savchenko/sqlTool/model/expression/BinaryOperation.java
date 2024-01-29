package com.savchenko.sqlTool.model.expression;

import com.savchenko.sqlTool.model.operator.Operator;

public record BinaryOperation(Operator operator, Expression<?> left, Expression<?> right) implements Expression<BinaryOperation> {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int compareTo(BinaryOperation binaryOperation) {
        return 0;
    }
}
