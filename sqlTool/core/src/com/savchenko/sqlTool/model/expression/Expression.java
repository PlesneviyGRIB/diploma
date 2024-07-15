package com.savchenko.sqlTool.model.expression;

import com.savchenko.sqlTool.model.domain.Column;
import com.savchenko.sqlTool.model.visitor.ExpressionPrinter;

public interface Expression {
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(ExpressionList list);

        T visit(SubTable table);

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

    default String stringify() {
        return this.accept(new ExpressionPrinter());
    }
}
