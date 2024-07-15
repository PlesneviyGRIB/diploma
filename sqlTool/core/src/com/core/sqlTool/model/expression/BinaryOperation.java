package com.core.sqlTool.model.expression;

import com.client.sqlTool.expression.Operator;

import java.util.Objects;

public record BinaryOperation(Operator operator, Expression left, Expression right) implements Expression {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinaryOperation that = (BinaryOperation) o;
        return Objects.equals(left, that.left) && Objects.equals(right, that.right) && operator == that.operator;
    }

    @Override
    public int hashCode() {
        return Objects.hash(operator, left, right);
    }
}
