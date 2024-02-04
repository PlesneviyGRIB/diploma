package com.savchenko.sqlTool.model.expression;

import com.savchenko.sqlTool.exception.UnexpectedException;
import com.savchenko.sqlTool.model.operator.Operator;

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

    @Override
    public Value<FloatNumber> processArithmetic(Operator operator, Value<FloatNumber> operand) {
        var val = (FloatNumber) operand;
        switch (operator) {
            case PLUS -> {
                new FloatNumber(this.value + val.value);
            }
            case MINUS -> {
                new FloatNumber(this.value - val.value);
            }
            case MULTIPLY -> {
                new FloatNumber(this.value * val.value);
            }
            case DIVISION -> {
                new FloatNumber(this.value / val.value);
            }
            case MOD -> {
                new FloatNumber(this.value % val.value);
            }
        }
        throw new UnexpectedException();
    }
}
