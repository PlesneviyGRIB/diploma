package com.savchenko.sqlTool.model.expression;

import com.savchenko.sqlTool.model.operator.Operator;

public record TernaryOperation(Operator operator, Expression first, Expression second, Expression third) implements Expression {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}
