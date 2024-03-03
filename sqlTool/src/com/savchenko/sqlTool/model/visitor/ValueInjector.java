package com.savchenko.sqlTool.model.visitor;

import com.savchenko.sqlTool.exception.UnexpectedException;
import com.savchenko.sqlTool.exception.UnexpectedExpressionException;
import com.savchenko.sqlTool.model.expression.ExpressionList;
import com.savchenko.sqlTool.model.domain.Column;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.model.expression.*;

import java.util.Map;
import java.util.Objects;

public class ValueInjector implements Expression.Visitor<Expression> {
    private final Map<Column, Value<?>> columnValue;
    private final Map<SubTable, Table> calculatedSubTables;

    public ValueInjector(Map<Column, Value<?>> values, Map<SubTable, Table> calculatedSubTables) {
        this.columnValue = values;
        this.calculatedSubTables = calculatedSubTables;
    }

    @Override
    public Expression visit(ExpressionList list) {
        return list;
    }

    @Override
    public Expression visit(Table table) {
        throw new UnexpectedExpressionException(table);
    }

    @Override
    public Expression visit(SubTable table) {
        var targetTable = calculatedSubTables.get(table);

        if (Objects.isNull(targetTable)) {
            throw new UnexpectedException("SubTable '%s' not found in context", table.stringify());
        }
        return targetTable;
    }

    @Override
    public Expression visit(Column column) {
        return columnValue.get(column);
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
