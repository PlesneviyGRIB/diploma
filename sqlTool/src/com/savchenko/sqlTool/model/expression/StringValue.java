package com.savchenko.sqlTool.model.expression;

public record StringValue(String value) implements Value<StringValue> {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int compareTo(StringValue stringValue) {
        return this.value().compareTo(stringValue.value());
    }

    @Override
    public String toString() {
        return value();
    }
}
