package com.core.sqlTool.model.expression;

import com.client.sqlTool.expression.Operator;
import com.core.sqlTool.exception.UnexpectedException;

import java.util.Objects;

public record FloatNumber(Float value) implements Value<FloatNumber> {
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
                return new FloatNumber(this.value + val.value);
            }
            case MINUS -> {
                return new FloatNumber(this.value - val.value);
            }
            case MULTIPLY -> {
                return new FloatNumber(this.value * val.value);
            }
            case DIVISION -> {
                return new FloatNumber(this.value / val.value);
            }
            case MOD -> {
                return new FloatNumber(this.value % val.value);
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
