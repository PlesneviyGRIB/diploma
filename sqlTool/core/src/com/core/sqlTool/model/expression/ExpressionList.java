package com.core.sqlTool.model.expression;

import java.util.List;
import java.util.Objects;

public record ExpressionList(List<? extends Value<?>> expressions, Class<? extends Value<?>> type) implements Expression {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpressionList that = (ExpressionList) o;
        return Objects.equals(type, that.type) && Objects.equals(expressions, that.expressions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expressions, type);
    }
}
