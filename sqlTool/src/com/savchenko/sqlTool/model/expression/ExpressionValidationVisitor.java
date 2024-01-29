package com.savchenko.sqlTool.model.expression;

import com.savchenko.sqlTool.exception.IncorrectOperatorUsageException;
import com.savchenko.sqlTool.model.structure.Column;
import com.savchenko.sqlTool.utils.ModelUtils;

import java.util.List;

public class ExpressionValidationVisitor implements Expression.Visitor<Void> {
    private final List<Column> columns;
    public ExpressionValidationVisitor(List<Column> columns) {
        this.columns = columns;
    }

    @Override
    public Void visit(Column column) {
        ModelUtils.resolveColumn(columns, column);
        return null;
    }

    @Override
    public Void visit(UnaryOperation operation) {
        if(!operation.operator().isUnary()){
            throw new IncorrectOperatorUsageException(operation.operator());
        }
        operation.expression().accept(this);
        return null;
    }

    @Override
    public Void visit(BinaryOperation operation) {
        if(!operation.operator().isBinary()){
            throw new IncorrectOperatorUsageException(operation.operator());
        }
        operation.left().accept(this);
        operation.right().accept(this);
        return null;
    }

    @Override
    public Void visit(TernaryOperation operation) {
        if(!operation.operator().isTernary()){
            throw new IncorrectOperatorUsageException(operation.operator());
        }
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
    public Void visit(IntegerNumber operation) {
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
