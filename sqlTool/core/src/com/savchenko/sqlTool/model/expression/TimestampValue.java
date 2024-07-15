package com.savchenko.sqlTool.model.expression;

import com.savchenko.sqlTool.exception.UnexpectedException;
import com.savchenko.sqlTool.model.operator.Operator;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Objects;

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
        throw new UnexpectedException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimestampValue that = (TimestampValue) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
