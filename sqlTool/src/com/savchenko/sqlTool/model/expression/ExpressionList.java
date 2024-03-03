package com.savchenko.sqlTool.model.expression;

import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.expression.Value;

import java.util.List;

public record ExpressionList(List<? extends Value<?>> expressions, Class<? extends Value<?>> type) implements Expression {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
