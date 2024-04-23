package com.savchenko.sqlTool.model.visitor;

import com.savchenko.sqlTool.model.domain.Column;
import com.savchenko.sqlTool.model.expression.*;

import java.util.stream.Collectors;

import static java.lang.String.format;

public class ExpressionPrinter implements Expression.Visitor<String> {

    @Override
    public String visit(ExpressionList list) {
        var args = list.expressions().stream()
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
        return format("COLUMN[%s]", column.toString());
    }

    @Override
    public String visit(UnaryOperation operation) {
        return format("%s(%s)", operation.operator().designator.toUpperCase(), operation.expression().accept(this));
    }

    @Override
    public String visit(BinaryOperation operation) {
        return format("%s %s %s", wrapWithParentheses(operation.left()), operation.operator().designator.toUpperCase(), wrapWithParentheses(operation.right()));
    }

    @Override
    public String visit(TernaryOperation operation) {
        return format("%s %s(%s and %s)",
                wrapWithParentheses(operation.first()),
                operation.operator().designator.toUpperCase(),
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
    public String visit(IntegerNumber value) {
        return value.value().toString();
    }

    @Override
    public String visit(LongNumber value) {
        return value.value().toString() + "L";
    }

    @Override
    public String visit(FloatNumber value) {
        return value.value().toString();
    }

    @Override
    public String visit(DoubleNumber value) {
        return value.value().toString();
    }

    @Override
    public String visit(BigDecimalNumber value) {
        return value.value().toString();
    }

    @Override
    public String visit(TimestampValue value) {
        return value.value().toString();
    }

    private String wrapWithParentheses(Expression expression) {
        if (expression instanceof Value<?> || expression instanceof SubTable || expression instanceof Column) {
            return expression.accept(this);
        }
        return format("(%s)", expression.accept(this));
    }
}
