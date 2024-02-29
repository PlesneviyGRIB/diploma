package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.expression.Value;

import java.util.List;

public record ExpressionList(List<Value> expressions) implements Expression {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
