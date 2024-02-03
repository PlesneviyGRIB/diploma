package com.savchenko.sqlTool.model.expression.visitor;

import com.savchenko.sqlTool.model.expression.*;
import com.savchenko.sqlTool.model.predicate.Predicate;
import com.savchenko.sqlTool.model.structure.Column;

public class ExpressionCalculator implements Expression.Visitor<Predicate> {

    @Override
    public Predicate visit(Column column) {
        return (columns, row) -> true;
    }

    @Override
    public Predicate visit(UnaryOperation operation) {
        return (columns, row) -> true;
    }

    @Override
    public Predicate visit(BinaryOperation operation) {
        return (columns, row) -> true;
    }

    @Override
    public Predicate visit(TernaryOperation operation) {
        return (columns, row) -> true;
    }

    @Override
    public Predicate visit(NullValue value) {
        return null;
    }

    @Override
    public Predicate visit(StringValue value) {
        return null;
    }

    @Override
    public Predicate visit(BooleanValue value) {
        return null;
    }

    @Override
    public Predicate visit(IntegerNumber operation) {
        return (columns, row) -> true;
    }

    @Override
    public Predicate visit(LongNumber value) {
        return null;
    }

    @Override
    public Predicate visit(FloatNumber value) {
        return null;
    }

    @Override
    public Predicate visit(DoubleNumber value) {
        return null;
    }

    @Override
    public Predicate visit(BigDecimalNumber value) {
        return null;
    }

    @Override
    public Predicate visit(TimestampValue value) {
        return null;
    }
}
