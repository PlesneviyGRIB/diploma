package com.core.sqlTool.model.visitor;

import com.core.sqlTool.model.command.domain.Command;
import com.core.sqlTool.model.command.domain.ComplexCalculatedCommand;
import com.core.sqlTool.model.command.domain.SimpleCalculatedCommand;
import com.core.sqlTool.model.command.domain.SimpleCommand;
import com.core.sqlTool.model.domain.Column;
import com.core.sqlTool.model.expression.*;

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
                    public Boolean visit(SimpleCalculatedCommand command) {
                        return false;
                    }

                    @Override
                    public Boolean visit(ComplexCalculatedCommand command) {
                        //return command.getExpression().accept(ContextSensitiveExpressionQualifier.this);
                        return false;
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
    public Boolean visit(NumberValue value) {
        return false;
    }

    @Override
    public Boolean visit(FloatNumberValue value) {
        return false;
    }

    @Override
    public Boolean visit(TimestampValue value) {
        return false;
    }

}
