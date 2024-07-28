package com.core.sqlTool.model.visitor;

import com.core.sqlTool.model.domain.Column;
import com.core.sqlTool.model.expression.*;

import java.util.stream.Collectors;

import static java.lang.String.format;

public class ExpressionPrinter implements Expression.Visitor<String> {

    @Override
    public String visit(ExpressionList expressionList) {
        var args = expressionList.expressions().stream()
                .map(e -> e.accept(this))
                .collect(Collectors.joining(", "));
        return format("List(%s)", args);
    }

    @Override
    public String visit(SubTable table) {
        return "SUB_TABLE[?]";
    }

    @Override
    public String visit(Column column) {
        return format("COLUMN(%s)", column);
    }

    @Override
    public String visit(UnaryOperation operation) {
        return String.format("%s(%s)", operation.operator().getDesignator().toUpperCase(), operation.expression().accept(this));
    }

    @Override
    public String visit(BinaryOperation operation) {
        return String.format("%s %s %s", wrapWithParentheses(operation.left()), operation.operator().getDesignator().toUpperCase(), wrapWithParentheses(operation.right()));
    }

    @Override
    public String visit(TernaryOperation operation) {
        return String.format("%s %s(%s and %s)",
                wrapWithParentheses(operation.first()),
                operation.operator().getDesignator().toUpperCase(),
                wrapWithParentheses(operation.second()),
                wrapWithParentheses(operation.third())
        );
    }

    @Override
    public String visit(NullValue value) {
        return "null";
    }

    @Override
    public String visit(StringValue value) {
        return value.value();
    }

    @Override
    public String visit(BooleanValue value) {
        return value.value().toString();
    }

    @Override
    public String visit(NumberValue value) {
        return value.value().toString();
    }

    @Override
    public String visit(FloatNumberValue value) {
        return value.value().toString();
    }

    @Override
    public String visit(TimestampValue value) {
        return value.value().toString();
    }

    @Override
    public String visit(NamedExpression value) {
        var isColumn = value.expression() instanceof Column;
        if (isColumn) {
            return value.expression().accept(this);
        }
        return "%s as %s".formatted(value.expression().accept(this), value.columnName());
    }

    private String wrapWithParentheses(Expression expression) {
        if (expression instanceof Value<?> || expression instanceof SubTable || expression instanceof Column) {
            return expression.accept(this);
        }
        return format("(%s)", expression.accept(this));
    }

}
