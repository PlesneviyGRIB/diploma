package com.savchenko.sqlTool.model.expression;

import com.savchenko.sqlTool.exception.UnexpectedException;
import com.savchenko.sqlTool.model.operator.Operator;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FloatNumber that = (FloatNumber) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
