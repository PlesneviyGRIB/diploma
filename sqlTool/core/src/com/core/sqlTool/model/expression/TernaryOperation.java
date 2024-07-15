package com.core.sqlTool.model.expression;

import com.client.sqlTool.expression.Operator;

import java.util.Objects;

public record TernaryOperation(Operator operator, Expression first, Expression second, Expression third) implements Expression {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TernaryOperation that = (TernaryOperation) o;
        return Objects.equals(first, that.first) && Objects.equals(third, that.third) && operator == that.operator && Objects.equals(second, that.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operator, first, second, third);
    }
}
