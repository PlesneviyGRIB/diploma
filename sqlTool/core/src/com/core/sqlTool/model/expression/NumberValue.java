package com.core.sqlTool.model.expression;

import com.client.sqlTool.expression.Operator;

public record NumberValue(Integer value) implements Value<NumberValue> {

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int compareTo(NumberValue integerNumber) {
        return this.value().compareTo(integerNumber.value());
    }

    @Override
    public Value<NumberValue> processArithmetic(Operator operator, Value<NumberValue> operand) {
        var val = (NumberValue) operand;
        switch (operator) {
            case PLUS -> {
                return new NumberValue(this.value + val.value);
            }
            case MINUS -> {
                return new NumberValue(this.value - val.value);
            }
            case MULTIPLY -> {
                return new NumberValue(this.value * val.value);
            }
            case DIVISION -> {
                return new NumberValue(this.value / val.value);
            }
            case MOD -> {
                return new NumberValue(this.value % val.value);
            }
        }

        return Value.super.processArithmetic(operator, operand);
    }

}
