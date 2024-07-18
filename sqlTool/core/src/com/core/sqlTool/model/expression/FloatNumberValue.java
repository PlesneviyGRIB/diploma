package com.core.sqlTool.model.expression;

import com.client.sqlTool.expression.Operator;
import com.core.sqlTool.exception.UnexpectedException;

import java.util.Objects;

public record FloatNumberValue(Float value) implements Value<FloatNumberValue> {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int compareTo(FloatNumberValue floatNumber) {
        return this.value().compareTo(floatNumber.value());
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
        throw new UnexpectedException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FloatNumberValue that = (FloatNumberValue) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
