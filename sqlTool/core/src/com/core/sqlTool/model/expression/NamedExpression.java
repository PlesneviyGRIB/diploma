package com.core.sqlTool.model.expression;

public record NamedExpression(Expression expression, String tableName, String columnName) implements Expression {

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}
