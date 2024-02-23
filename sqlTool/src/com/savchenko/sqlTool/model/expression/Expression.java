package com.savchenko.sqlTool.model.expression;

import com.savchenko.sqlTool.model.structure.Column;
import com.savchenko.sqlTool.model.structure.Table;

public interface Expression <O> extends Comparable<O> {
    <T> T accept(Visitor<T> visitor);
    interface Visitor<T> {
        T visit(Table table);
        T visit(Column column);
        T visit(UnaryOperation operation);
        T visit(BinaryOperation operation);
        T visit(TernaryOperation operation);
        T visit(NullValue value);
        T visit(StringValue value);
        T visit(BooleanValue value);
        T visit(IntegerNumber value);
        T visit(LongNumber value);
        T visit(FloatNumber value);
        T visit(DoubleNumber value);
        T visit(BigDecimalNumber value);
        T visit(TimestampValue value);
    }
}
