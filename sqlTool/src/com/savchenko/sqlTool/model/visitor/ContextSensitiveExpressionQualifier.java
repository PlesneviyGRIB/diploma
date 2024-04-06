package com.savchenko.sqlTool.model.visitor;

import com.savchenko.sqlTool.model.domain.Column;
import com.savchenko.sqlTool.model.domain.ExternalRow;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.model.expression.*;
import com.savchenko.sqlTool.model.resolver.Resolver;

public class ContextSensitiveExpressionQualifier implements Expression.Visitor<Boolean> {

    private final Resolver resolver;

    private final Table externalTable;

    public ContextSensitiveExpressionQualifier(Resolver resolver, Table externalTable) {
        this.resolver = resolver;
        this.externalTable = externalTable;
    }

    @Override
    public Boolean visit(ExpressionList list) {
        return false;
    }

    @Override
    public Boolean visit(SubTable table) {
        var externalRow = getFullExternalRow(externalTable);
        resolver.resolve(table.commands(), externalRow);
        return externalRow.isUsed();
    }

    @Override
    public Boolean visit(Column column) {
        return true;
    }

    @Override
    public Boolean visit(UnaryOperation operation) {
        return operation.expression().accept(this);
    }

    @Override
    public Boolean visit(BinaryOperation operation) {
        return operation.left().accept(this) || operation.right().accept(this);
    }

    @Override
    public Boolean visit(TernaryOperation operation) {
        return operation.first().accept(this)
                || operation.second().accept(this)
                || operation.third().accept(this);
    }

    @Override
    public Boolean visit(NullValue value) {
        return false;
    }

    @Override
    public Boolean visit(StringValue value) {
        return false;
    }

    @Override
    public Boolean visit(BooleanValue value) {
        return false;
    }

    @Override
    public Boolean visit(IntegerNumber value) {
        return false;
    }

    @Override
    public Boolean visit(LongNumber value) {
        return false;
    }

    @Override
    public Boolean visit(FloatNumber value) {
        return false;
    }

    @Override
    public Boolean visit(DoubleNumber value) {
        return false;
    }

    @Override
    public Boolean visit(BigDecimalNumber value) {
        return false;
    }

    @Override
    public Boolean visit(TimestampValue value) {
        return false;
    }

    private ExternalRow getFullExternalRow(Table table) {
        var externalRow = table.data().isEmpty() ? ExternalRow.empty() : new ExternalRow(table.columns(), table.data().get(0));
        return table.externalRow().merge(externalRow).deepCopy();
    }
}
