package com.savchenko.sqlTool.model.expression;

import com.savchenko.sqlTool.exception.UnexpectedException;
import com.savchenko.sqlTool.model.operator.Operator;

import java.util.Objects;

public record IntegerNumber(Integer value) implements Value<IntegerNumber> {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int compareTo(IntegerNumber integerNumber) {
        return this.value().compareTo(integerNumber.value());
    }

    @Override
    public Value<IntegerNumber> processArithmetic(Operator operator, Value<IntegerNumber> operand) {
        var val = (IntegerNumber) operand;
        switch (operator) {
            case PLUS -> {
                return new IntegerNumber(this.value + val.value);
            }
            case MINUS -> {
                return new IntegerNumber(this.value - val.value);
            }
            case MULTIPLY -> {
                return new IntegerNumber(this.value * val.value);
            }
            case DIVISION -> {
                return new IntegerNumber(this.value / val.value);
            }
            case MOD -> {
                return new IntegerNumber(this.value % val.value);
            }
        }
        throw new UnexpectedException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntegerNumber that = (IntegerNumber) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
