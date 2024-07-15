package com.savchenko.sqlTool.model.expression;

import com.savchenko.sqlTool.exception.UnexpectedException;
import com.savchenko.sqlTool.model.operator.Operator;

import java.util.Objects;

public record DoubleNumber(Double value) implements Value<DoubleNumber> {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int compareTo(DoubleNumber doubleNumber) {
        return this.value().compareTo(doubleNumber.value());
    }

    @Override
    public Value<DoubleNumber> processArithmetic(Operator operator, Value<DoubleNumber> operand) {
        var val = (DoubleNumber) operand;
        switch (operator) {
            case PLUS -> {
                return new DoubleNumber(this.value + val.value);
            }
            case MINUS -> {
                return new DoubleNumber(this.value - val.value);
            }
            case MULTIPLY -> {
                return new DoubleNumber(this.value * val.value);
            }
            case DIVISION -> {
                return new DoubleNumber(this.value / val.value);
            }
            case MOD -> {
                return new DoubleNumber(this.value % val.value);
            }
        }
        throw new UnexpectedException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DoubleNumber that = (DoubleNumber) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
