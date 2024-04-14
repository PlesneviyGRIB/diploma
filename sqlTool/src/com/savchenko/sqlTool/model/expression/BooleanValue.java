package com.savchenko.sqlTool.model.expression;

import java.util.Objects;

public record BooleanValue(Boolean value) implements Value<BooleanValue> {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int compareTo(BooleanValue booleanValue) {
        return this.value().compareTo(booleanValue.value());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BooleanValue that = (BooleanValue) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
