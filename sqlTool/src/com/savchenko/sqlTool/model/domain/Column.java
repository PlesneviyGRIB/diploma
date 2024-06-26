package com.savchenko.sqlTool.model.domain;

import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.expression.Value;

import java.util.Objects;

public record Column(String name, String table, Class<? extends Value<?>> type) implements Expression {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Column column = (Column) o;
        return Objects.equals(name, column.name) && Objects.equals(table, column.table);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, table);
    }

    @Override
    public String toString() {
        return table + "." + name;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}
