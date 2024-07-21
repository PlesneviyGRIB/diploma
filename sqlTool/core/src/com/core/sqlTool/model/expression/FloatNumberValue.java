package com.core.sqlTool.model.expression;

import com.client.sqlTool.expression.Operator;

public record FloatNumberValue(Float value) implements Value<FloatNumberValue> {

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Value<FloatNumberValue> processArithmetic(Operator operator, Value<FloatNumberValue> operand) {
        var val = (FloatNumberValue) operand;
        switch (operator) {
            case PLUS -> {
                return new FloatNumberValue(this.value + val.value);
            }
            case MINUS -> {
                return new FloatNumberValue(this.value - val.value);
            }
            case MULTIPLY -> {
                return new FloatNumberValue(this.value * val.value);
            }
            case DIVISION -> {
                return new FloatNumberValue(this.value / val.value);
            }
            case MOD -> {
                return new FloatNumberValue(this.value % val.value);
            }
        }

        return Value.super.processArithmetic(operator, operand);
    }

    @Override
    public int compareTo(FloatNumberValue floatNumberValue) {
        return value.compareTo(floatNumberValue.value);
    }

}
