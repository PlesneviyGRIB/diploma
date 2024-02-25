package com.savchenko.sqlTool.model.expression;

import com.savchenko.sqlTool.exception.UnexpectedException;
import com.savchenko.sqlTool.model.operator.Operator;

public record LongNumber(Long value) implements Value<LongNumber> {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int compareTo(LongNumber longNumber) {
        return this.value().compareTo(longNumber.value());
    }

    @Override
    public String toString() {
        return value().toString();
    }

    @Override
    public Value<LongNumber> processArithmetic(Operator operator, Value<LongNumber> operand) {
        var val = (LongNumber) operand;
        switch (operator) {
            case PLUS -> {
                return new LongNumber(this.value + val.value());
            }
            case MINUS -> {
                return new LongNumber(this.value - val.value());
            }
            case MULTIPLY -> {
                return new LongNumber(this.value * val.value());
            }
            case DIVISION -> {
                return new LongNumber(this.value / val.value());
            }
            case MOD -> {
                return new LongNumber(this.value % val.value());
            }
        }
        throw new UnexpectedException();
    }
}