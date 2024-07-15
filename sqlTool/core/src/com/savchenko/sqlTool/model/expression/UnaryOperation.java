package com.savchenko.sqlTool.model.expression;

import com.savchenko.sqlTool.model.operator.Operator;

import java.util.Objects;

public record UnaryOperation(Operator operator, Expression expression) implements Expression {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnaryOperation that = (UnaryOperation) o;
        return operator == that.operator && Objects.equals(expression, that.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operator, expression);
    }
}
