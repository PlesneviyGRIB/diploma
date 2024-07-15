package com.savchenko.sqlTool.model.expression;

import com.savchenko.sqlTool.exception.UnexpectedException;
import com.savchenko.sqlTool.model.operator.Operator;

import java.math.BigDecimal;
import java.util.Objects;

public record BigDecimalNumber(BigDecimal value) implements Value<BigDecimalNumber> {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int compareTo(BigDecimalNumber bigDecimalNumber) {
        return this.value().compareTo(bigDecimalNumber.value());
    }

    @Override
    public Value<BigDecimalNumber> processArithmetic(Operator operator, Value<BigDecimalNumber> operand) {
        var val = (BigDecimalNumber) operand;
        switch (operator) {
            case PLUS -> {
                return new BigDecimalNumber(this.value.add(val.value));
            }
            case MINUS -> {
                return new BigDecimalNumber(this.value.subtract(val.value));
            }
            case MULTIPLY -> {
                return new BigDecimalNumber(this.value.multiply(val.value));
            }
            case DIVISION -> {
                return new BigDecimalNumber(this.value.divide(val.value));
            }
            case MOD -> {
                return new BigDecimalNumber(this.value.remainder(val.value));
            }
        }
        throw new UnexpectedException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BigDecimalNumber that = (BigDecimalNumber) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
