package com.savchenko.sqlTool.model.expression;

import com.savchenko.sqlTool.exception.UnexpectedException;
import com.savchenko.sqlTool.model.operator.Operator;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LongNumber that = (LongNumber) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
