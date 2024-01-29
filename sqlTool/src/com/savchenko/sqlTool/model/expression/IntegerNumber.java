package com.savchenko.sqlTool.model.expression;

public record IntegerNumber(Integer value) implements Value<IntegerNumber> {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int compareTo(IntegerNumber integerNumber) {
        return this.value().compareTo(integerNumber.value());
    }

    @Override
    public String toString() {
        return value().toString();
    }
}
