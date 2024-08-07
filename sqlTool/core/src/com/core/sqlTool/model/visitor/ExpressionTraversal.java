package com.core.sqlTool.model.visitor;

import com.core.sqlTool.model.domain.Column;
import com.core.sqlTool.model.expression.NumberValue;
import com.core.sqlTool.model.expression.*;

public class ExpressionTraversal implements Expression.Visitor<Void> {

    @Override
    public Void visit(ExpressionList list) {
        list.expressions().forEach(expression -> expression.accept(this));
        return null;
    }

    @Override
    public Void visit(SubQuery table) {
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
    public Void visit(NumberValue value) {
        return null;
    }

    @Override
    public Void visit(FloatNumberValue value) {
        return null;
    }

    @Override
    public Void visit(TimestampValue value) {
        return null;
    }

    @Override
    public Void visit(NamedExpression value) {
        return value.expression().accept(this);
    }

}
