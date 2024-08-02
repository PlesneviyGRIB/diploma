package com.core.sqlTool.model.expression;

import java.util.List;

public record ExpressionList(List<Expression> expressions) implements Expression {

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}
