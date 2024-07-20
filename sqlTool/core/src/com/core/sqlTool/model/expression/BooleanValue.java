package com.core.sqlTool.model.expression;

public record BooleanValue(Boolean value) implements Value<BooleanValue> {

    @Override
    public int compareTo(BooleanValue booleanValue) {
        return this.value().compareTo(booleanValue.value());
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}
