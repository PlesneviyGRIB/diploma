package com.savchenko.sqlTool.model.expression;

import com.savchenko.sqlTool.exception.UnexpectedException;
import com.savchenko.sqlTool.model.operator.Operator;

public record StringValue(String value) implements Value<StringValue> {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int compareTo(StringValue stringValue) {
        return this.value().compareTo(stringValue.value());
    }

    @Override
    public String toString() {
        return value();
    }

    @Override
    public Value<StringValue> processArithmetic(Operator operator, Value<StringValue> operand) {
        if(operator == Operator.PLUS) {
            return new StringValue(this.value + ((StringValue) operand).value);
        }
        throw new UnexpectedException();
    }
}
