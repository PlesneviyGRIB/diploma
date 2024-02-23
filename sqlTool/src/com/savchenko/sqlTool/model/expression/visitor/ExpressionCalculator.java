package com.savchenko.sqlTool.model.expression.visitor;

import com.savchenko.sqlTool.exception.UnexpectedException;
import com.savchenko.sqlTool.exception.UnsupportedTypeException;
import com.savchenko.sqlTool.model.expression.*;
import com.savchenko.sqlTool.model.structure.Column;
import com.savchenko.sqlTool.model.structure.Table;

import static com.savchenko.sqlTool.model.operator.Operator.EXISTS;
import static com.savchenko.sqlTool.model.operator.Operator.IS_NULL;

public class ExpressionCalculator implements Expression.Visitor<Value<?>> {

    @Override
    public Value<?> visit(Table table) {
        throw new UnsupportedTypeException();
    }

    @Override
    public Value<Column> visit(Column column) {
        throw new UnexpectedException();
    }

    @Override
    public Value<BooleanValue> visit(UnaryOperation operation) {
        var op = operation.operator();
        var value = operation.expression().accept(this);
        if(op == EXISTS) {
            return new BooleanValue(!(value instanceof NullValue));
        }
        if(op == IS_NULL) {
            return new BooleanValue(value instanceof NullValue);
        }
        throw new UnexpectedException();
    }

    @Override
    public Value<?> visit(BinaryOperation operation) {
        var left = operation.left().accept(this);
        var right = operation.right().accept(this);
        var targetClass = left.getClass();
        var l = targetClass.cast(left);
        var r = targetClass.cast(right);

        switch (operation.operator()) {
            case AND -> {
                var val1 = BooleanValue.class.cast(left);
                var val2 = BooleanValue.class.cast(right);
                return new BooleanValue(val1.value() && val2.value());
            }
            //TODO case IN -> throw new UnexpectedException();
            case OR -> {
                var val1 = BooleanValue.class.cast(left);
                var val2 = BooleanValue.class.cast(right);
                return new BooleanValue(val1.value() || val2.value());
            }
            case EQ -> {
                return new BooleanValue(l.compareTo(r) == 0);
            }
            case NOT_EQ -> {
                return new BooleanValue(l.compareTo(r) != 0);
            }
            case GREATER_OR_EQ -> {
                return new BooleanValue(l.compareTo(r) >= 0);
            }
            case LESS_OR_EQ -> {
                return new BooleanValue(l.compareTo(r) <= 0);
            }
            case GREATER -> {
                return new BooleanValue(l.compareTo(r) > 0);
            }
            case LESS -> {
                return new BooleanValue(l.compareTo(r) < 0);
            }
            case PLUS, MINUS, MULTIPLY, DIVISION, MOD -> {
                return l.processArithmetic(operation.operator(), r);
            }
            default -> throw new UnexpectedException();
        }
    }

    @Override
    public Value<BooleanValue> visit(TernaryOperation operation) {
        var f = operation.first().accept(this);
        var targetClass = f.getClass();
        var first = targetClass.cast(f);
        var second = targetClass.cast(operation.second().accept(this));
        var third = targetClass.cast(operation.third().accept(this));
        return new BooleanValue(second.compareTo(first) <= 0 && first.compareTo(third) < 0);
    }

    @Override
    public Value<Object> visit(NullValue value) {
        return value;
    }

    @Override
    public Value<StringValue> visit(StringValue value) {
        return value;
    }

    @Override
    public Value<BooleanValue> visit(BooleanValue value) {
        return value;
    }

    @Override
    public Value<IntegerNumber> visit(IntegerNumber value) {
        return value;
    }

    @Override
    public Value<LongNumber> visit(LongNumber value) {
        return value;
    }

    @Override
    public Value<FloatNumber> visit(FloatNumber value) {
        return value;
    }

    @Override
    public Value<DoubleNumber> visit(DoubleNumber value) {
        return value;
    }

    @Override
    public Value<BigDecimalNumber> visit(BigDecimalNumber value) {
        return value;
    }

    @Override
    public Value<TimestampValue> visit(TimestampValue value) {
        return value;
    }
}
