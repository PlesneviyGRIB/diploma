package com.savchenko.sqlTool.model.expression;

public record LongNumber(Long value) implements Value<LongNumber> {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int compareTo(LongNumber longNumber) {
        return this.value().compareTo(longNumber.value());
    }

    @Override
    public String toString() {
        return value().toString();
    }
}
