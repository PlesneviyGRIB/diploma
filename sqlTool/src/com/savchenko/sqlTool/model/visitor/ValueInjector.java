package com.savchenko.sqlTool.model.visitor;

import com.savchenko.sqlTool.exception.UnexpectedException;
import com.savchenko.sqlTool.model.domain.Column;
import com.savchenko.sqlTool.model.domain.ExternalHeaderRow;
import com.savchenko.sqlTool.model.domain.HeaderRow;
import com.savchenko.sqlTool.model.expression.*;

public class ValueInjector implements Expression.Visitor<Expression> {

    private final HeaderRow HeaderRow;

    private final ExternalHeaderRow externalRow;

    public ValueInjector(HeaderRow HeaderRow, ExternalHeaderRow externalRow) {
        this.HeaderRow = HeaderRow;
        this.externalRow = externalRow;
    }

    @Override
    public Expression visit(ExpressionList list) {
        return list;
    }

    @Override
    public Expression visit(SubTable table) {
        return table;
    }

    @Override
    public Expression visit(Column column) {
        return HeaderRow.getValue(column).or(() -> externalRow.getValue(column)).orElseThrow(UnexpectedException::new);
    }

    @Override
    public Expression visit(UnaryOperation operation) {
        return new UnaryOperation(
                operation.operator(),
                operation.expression().accept(this)
        );
    }

    @Override
    public Expression visit(BinaryOperation operation) {
        return new BinaryOperation(
                operation.operator(),
                operation.left().accept(this),
                operation.right().accept(this)
        );
    }

    @Override
    public Expression visit(TernaryOperation operation) {
        return new TernaryOperation(
                operation.operator(),
                operation.first().accept(this),
                operation.second().accept(this),
                operation.third().accept(this)
        );
    }

    @Override
    public Expression visit(NullValue value) {
        return value;
    }

    @Override
    public Expression visit(StringValue value) {
        return value;
    }

    @Override
    public Expression visit(BooleanValue value) {
        return value;
    }

    @Override
    public Expression visit(IntegerNumber value) {
        return value;
    }

    @Override
    public Expression visit(LongNumber value) {
        return value;
    }

    @Override
    public Expression visit(FloatNumber value) {
        return value;
    }

    @Override
    public Expression visit(DoubleNumber value) {
        return value;
    }

    @Override
    public Expression visit(BigDecimalNumber value) {
        return value;
    }

    @Override
    public Expression visit(TimestampValue value) {
        return value;
    }
}
