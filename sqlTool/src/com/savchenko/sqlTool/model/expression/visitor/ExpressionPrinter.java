package com.savchenko.sqlTool.model.expression.visitor;

import com.savchenko.sqlTool.model.expression.*;
import com.savchenko.sqlTool.model.structure.Column;
import com.savchenko.sqlTool.model.structure.Table;

import static java.lang.String.format;

public class ExpressionPrinter implements Expression.Visitor<String> {
    @Override
    public String visit(Table table) {
        return format("TABLE[%s]", table.name());
    }

    @Override
    public String visit(Column column) {
        return format("COLUMN[%s]", column.toString());
    }

    @Override
    public String visit(UnaryOperation operation) {
        return format("%s(%s)", operation.operator().designator, operation.expression().accept(this));
    }

    @Override
    public String visit(BinaryOperation operation) {
        return format("%s %s %s", wrapWithParentheses(operation.left()), operation.operator().designator, wrapWithParentheses(operation.right()));
    }

    @Override
    public String visit(TernaryOperation operation) {
        return format("%s %s(%s and %s)",
                wrapWithParentheses(operation.first()),
                operation.operator().designator,
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
        return value.value().toString();
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

    private String wrapWithParentheses(Expression<?> expression) {
        if(expression instanceof Value<?> || expression instanceof Table || expression instanceof Column) {
            return expression.accept(this);
        }
        return format("(%s)", expression.accept(this));
    }
}
