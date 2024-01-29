package com.savchenko.sqlTool.model.expression;

import java.math.BigDecimal;

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
    public String toString() {
        return value().toString();
    }
}
