package com.savchenko.sqlTool.model;

import java.util.Objects;

public record Column(String name, String table, Class<? extends Comparable<?>> type) {
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
}
