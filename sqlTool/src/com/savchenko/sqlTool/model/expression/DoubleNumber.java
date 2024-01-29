package com.savchenko.sqlTool.model.expression;

public record DoubleNumber(Double value) implements Value<DoubleNumber> {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int compareTo(DoubleNumber doubleNumber) {
        return this.value().compareTo(doubleNumber.value());
    }

    @Override
    public String toString() {
        return value().toString();
    }
}
