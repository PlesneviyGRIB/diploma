package com.savchenko.sqlTool.model.visitor;

import com.savchenko.sqlTool.model.expression.ExpressionList;
import com.savchenko.sqlTool.model.expression.*;
import com.savchenko.sqlTool.model.domain.Column;

public class ExpressionTraversal implements Expression.Visitor<Void> {

    @Override
    public Void visit(ExpressionList list) {
        list.expressions().forEach(expression -> expression.accept(this));
        return null;
    }

    @Override
    public Void visit(SubTable table) {
        return null;
    }

    @Override
    public Void visit(Column column) {
        return null;
    }

    @Override
    public Void visit(UnaryOperation operation) {
        operation.expression().accept(this);
        return null;
    }

    @Override
    public Void visit(BinaryOperation operation) {
        operation.left().accept(this);
        operation.right().accept(this);
        return null;
    }

    @Override
    public Void visit(TernaryOperation operation) {
        operation.first().accept(this);
        operation.second().accept(this);
        operation.third().accept(this);
        return null;
    }

    @Override
    public Void visit(NullValue value) {
        return null;
    }

    @Override
    public Void visit(StringValue value) {
        return null;
    }

    @Override
    public Void visit(BooleanValue value) {
        return null;
    }

    @Override
    public Void visit(IntegerNumber value) {
        return null;
    }

    @Override
    public Void visit(LongNumber value) {
        return null;
    }

    @Override
    public Void visit(FloatNumber value) {
        return null;
    }

    @Override
    public Void visit(DoubleNumber value) {
        return null;
    }

    @Override
    public Void visit(BigDecimalNumber value) {
        return null;
    }

    @Override
    public Void visit(TimestampValue value) {
        return null;
    }
}
