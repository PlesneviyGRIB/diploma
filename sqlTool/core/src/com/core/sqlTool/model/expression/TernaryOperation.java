package com.core.sqlTool.model.expression;

import com.client.sqlTool.expression.Operator;

public record TernaryOperation(Operator operator, Expression first, Expression second, Expression third) implements Expression {

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}
