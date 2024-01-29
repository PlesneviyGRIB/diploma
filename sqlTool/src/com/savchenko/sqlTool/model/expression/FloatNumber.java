package com.savchenko.sqlTool.model.expression;

public record FloatNumber(Float value) implements Value<FloatNumber>{
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int compareTo(FloatNumber floatNumber) {
        return this.value().compareTo(floatNumber.value());
    }

    @Override
    public String toString() {
        return value().toString();
    }
}
