package com.core.sqlTool.model.expression;

import com.client.sqlTool.expression.Operator;
import com.core.sqlTool.exception.UnexpectedException;

import java.util.Objects;

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
        throw new UnexpectedException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NumberValue that = (NumberValue) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
