package com.savchenko.sqlTool.model.visitor;

import com.savchenko.sqlTool.model.command.domain.Command;
import com.savchenko.sqlTool.model.command.domain.ComplexCalculedCommand;
import com.savchenko.sqlTool.model.command.domain.SimpleCalculedCommand;
import com.savchenko.sqlTool.model.command.domain.SimpleCommand;
import com.savchenko.sqlTool.model.domain.Column;
import com.savchenko.sqlTool.model.domain.ExternalRow;
import com.savchenko.sqlTool.model.expression.*;
import com.savchenko.sqlTool.model.resolver.Resolver;

import java.util.List;

public class ContextSensitiveExpressionQualifier implements Expression.Visitor<Boolean> {

    private final List<Column> columns;

    public ContextSensitiveExpressionQualifier(List<Column> columns) {
        this.columns = columns;
    }

    @Override
    public Boolean visit(ExpressionList list) {
        return false;
    }

    @Override
    public Boolean visit(SubTable table) {
        return table.commands().stream()
                .anyMatch(command -> command.accept(new Command.Visitor<>() {
                    @Override
                    public Boolean visit(SimpleCommand command) {
                        return false;
                    }

                    @Override
                    public Boolean visit(SimpleCalculedCommand command) {
                        return false;
                    }

                    @Override
                    public Boolean visit(ComplexCalculedCommand command) {
                        return command.getExpression().accept(ContextSensitiveExpressionQualifier.this);
                    }
                }));
    }

    @Override
    public Boolean visit(Column column) {
        return columns.stream().anyMatch(c -> c.equals(column));
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

}
