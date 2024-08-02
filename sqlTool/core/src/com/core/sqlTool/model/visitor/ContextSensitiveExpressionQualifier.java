package com.core.sqlTool.model.visitor;

import com.core.sqlTool.model.command.*;
import com.core.sqlTool.model.domain.Column;
import com.core.sqlTool.model.expression.*;
import com.core.sqlTool.utils.ModelUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.tuple.Pair;

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
    public Boolean visit(SubQuery table) {
        return table.commands().stream()
                .anyMatch(command -> command.accept(new Command.Visitor<>() {

                    @Override
                    public Boolean visit(ConstructIndexCommand command) {
                        return false;
                    }

                    @Override
                    public Boolean visit(DistinctCommand command) {
                        return false;
                    }

                    @Override
                    public Boolean visit(FromCommand command) {
                        return false;
                    }

                    @Override
                    public Boolean visit(GroupByCommand command) {
                        return ListUtils.union(command.expressions(), command.aggregations().stream().map(Pair::getKey).toList()).stream()
                                .anyMatch(expression -> expression.accept(ContextSensitiveExpressionQualifier.this));
                    }

                    @Override
                    public Boolean visit(LimitCommand command) {
                        return false;
                    }

                    @Override
                    public Boolean visit(OffsetCommand command) {
                        return false;
                    }

                    @Override
                    public Boolean visit(OrderByCommand command) {
                        return command.orders().stream()
                                .map(Pair::getKey)
                                .anyMatch(expression -> expression.accept(ContextSensitiveExpressionQualifier.this));
                    }

                    @Override
                    public Boolean visit(SelectCommand command) {
                        return command.expressions().stream()
                                .anyMatch(expression -> expression.accept(ContextSensitiveExpressionQualifier.this));
                    }

                    @Override
                    public Boolean visit(TableAliasCommand command) {
                        return false;
                    }

                    @Override
                    public Boolean visit(WhereCommand command) {
                        return command.expression().accept(ContextSensitiveExpressionQualifier.this);
                    }

                    @Override
                    public Boolean visit(JoinCommand command) {
                        return command.getExpression().accept(ContextSensitiveExpressionQualifier.this);
                    }

                }));
    }

    @Override
    public Boolean visit(Column column) {
        return ModelUtils.columnIndex(columns, column).isPresent();
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

    @Override
    public Boolean visit(NamedExpression value) {
        return value.expression().accept(this);
    }

}
