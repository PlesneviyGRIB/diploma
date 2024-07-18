package com.core.sqlTool.model.domain;

import com.core.sqlTool.model.expression.Expression;
import com.core.sqlTool.model.expression.Value;

import java.util.Objects;

public record Column(String tableName, String columnName, Class<? extends Value<?>> columnType) implements Expression {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Column column = (Column) o;
        return Objects.equals(column, column.columnName) && Objects.equals(tableName, column.tableName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnName, tableName);
    }

    @Override
    public String toString() {
        return tableName + "." + columnName;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}
