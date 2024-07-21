package com.core.sqlTool.model.expression;

import com.client.sqlTool.expression.Operator;

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
    public Value<StringValue> processArithmetic(Operator operator, Value<StringValue> operand) {

        if (operator == Operator.PLUS) {
            return new StringValue(this.value + ((StringValue) operand).value);
        }

        return Value.super.processArithmetic(operator, operand);
    }

}
