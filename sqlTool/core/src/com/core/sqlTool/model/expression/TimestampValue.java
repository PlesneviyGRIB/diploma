package com.core.sqlTool.model.expression;

import com.client.sqlTool.expression.Operator;

import java.sql.Timestamp;
import java.util.Calendar;

public record TimestampValue(Timestamp value) implements Value<TimestampValue> {

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int compareTo(TimestampValue timestampValue) {
        return this.value().compareTo(timestampValue.value());
    }

    @Override
    public Value<TimestampValue> processArithmetic(Operator operator, Value<TimestampValue> operand) {
        var val = (TimestampValue) operand;
        var calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.value.getTime());

        switch (operator) {
            case PLUS -> {
                calendar.add(Calendar.MILLISECOND, (int) val.value().getTime());
                new TimestampValue(new Timestamp(calendar.getTime().getTime()));
            }
            case MINUS -> {
                calendar.add(Calendar.MILLISECOND, -(int) val.value().getTime());
                new TimestampValue(new Timestamp(calendar.getTime().getTime()));
            }
        }

        return Value.super.processArithmetic(operator, operand);
    }

}
