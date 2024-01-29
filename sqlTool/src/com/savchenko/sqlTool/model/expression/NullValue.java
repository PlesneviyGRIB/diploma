package com.savchenko.sqlTool.model.expression;

public record NullValue() implements Value<Object> {
    @Override
    public <T> T accept(Expression.Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int compareTo(Object value) {
        if(value instanceof NullValue) {
            return 0;
        }
        return -1;
    }

    @Override
    public String toString() {
        return "null";
    }
}
