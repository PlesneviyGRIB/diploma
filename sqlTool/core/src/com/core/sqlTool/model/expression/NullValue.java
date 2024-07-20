package com.core.sqlTool.model.expression;

public record NullValue() implements Value<Object> {

    @Override
    public <T> T accept(Expression.Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int compareTo(Object value) {
        return this.equals(value) ? 0 : -1;
    }

}
