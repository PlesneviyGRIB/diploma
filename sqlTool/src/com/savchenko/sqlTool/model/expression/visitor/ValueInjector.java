package com.savchenko.sqlTool.model.expression.visitor;

import com.savchenko.sqlTool.model.expression.*;
import com.savchenko.sqlTool.model.structure.Column;
import com.savchenko.sqlTool.utils.ModelUtils;

import java.util.List;

public class ValueInjector implements Expression.Visitor<Expression<?>> {
    private final List<Column> columns;
    private final List<Value<?>> values;

    public ValueInjector(List<Column> columns, List<Value<?>> values) {
        this.columns = columns;
        this.values = values;
    }

    @Override
    public Expression<?> visit(Column column) {
        var index = ModelUtils.resolveColumnIndex(columns, column);
        return values.get(index);
    }

    @Override
    public Expression<UnaryOperation> visit(UnaryOperation operation) {
        return new UnaryOperation(
                operation.operator(),
                operation.expression().accept(this)
        );
    }

    @Override
    public Expression<BinaryOperation> visit(BinaryOperation operation) {
        return new BinaryOperation(
                operation.operator(),
                operation.left().accept(this),
                operation.right().accept(this)
        );
    }

    @Override
    public Expression<TernaryOperation> visit(TernaryOperation operation) {
        return new TernaryOperation(
                operation.operator(),
                operation.first().accept(this),
                operation.second().accept(this),
                operation.third().accept(this)
        );
    }

    @Override
    public Expression<Object> visit(NullValue value) {
        return value;
    }

    @Override
    public Expression<StringValue> visit(StringValue value) {
        return value;
    }

    @Override
    public Expression<BooleanValue> visit(BooleanValue value) {
        return value;
    }

    @Override
    public Expression<IntegerNumber> visit(IntegerNumber value) {
        return value;
    }

    @Override
    public Expression<LongNumber> visit(LongNumber value) {
        return value;
    }

    @Override
    public Expression<FloatNumber> visit(FloatNumber value) {
        return value;
    }

    @Override
    public Expression<DoubleNumber> visit(DoubleNumber value) {
        return value;
    }

    @Override
    public Expression<BigDecimalNumber> visit(BigDecimalNumber value) {
        return value;
    }

    @Override
    public Expression<TimestampValue> visit(TimestampValue value) {
        return value;
    }
}
