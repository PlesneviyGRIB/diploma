package com.core.sqlTool.model.domain;

import com.core.sqlTool.model.expression.Expression;
import com.core.sqlTool.model.expression.Value;

public record Column(String tableName, String columnName, Class<? extends Value<?>> columnType) implements Expression {

    @Override
    public String toString() {
        return tableName + "." + columnName;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}
