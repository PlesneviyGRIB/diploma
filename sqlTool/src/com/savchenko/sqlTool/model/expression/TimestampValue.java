package com.savchenko.sqlTool.model.expression;

import java.sql.Timestamp;

public record TimestampValue(Timestamp value) implements Value<TimestampValue> {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int compareTo(TimestampValue timestampValue) {
        return this.value().compareTo(timestampValue.value());
    }

    @Override
    public String toString() {
        return value().toString();
    }
}
