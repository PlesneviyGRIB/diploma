package com.core.sqlTool.model.expression;

public record NamedExpression(Expression expression, String name) implements Expression {

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}
