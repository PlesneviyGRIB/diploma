package com.savchenko.sqlTool.model.structure;

import com.savchenko.sqlTool.exception.UnexpectedException;
import com.savchenko.sqlTool.model.command.OrderSpecifier;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.expression.Value;

import java.util.Objects;

public record Column(String name, String table, Class<? extends Value<?>> type) implements Expression<Column>, OrderSpecifier {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Column column = (Column) o;
        return Objects.equals(name, column.name) && Objects.equals(table, column.table) && Objects.equals(type, column.type);
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

    @Override
    public int compareTo(Column column) {
        throw new UnexpectedException("Unexpect usage of compareTo method with column");
    }
}