package com.savchenko.sqlTool.model.structure;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

public record Table(String name, List<Column> columns, List<List<Comparable<?>>> data) {

    public Column getColumn(Column column) {
        return getColumn(column.table(), column.name());
    }
    public Column getColumn(String table, String name) {
        return columns.stream().filter(c -> c.table().equals(table) && c.name().equals(name)).findFirst()
                .orElseThrow(() -> new RuntimeException(format("Unable to find column '%s' in table '%s' [%s]", table + "." + name,
                        this.name, columns.stream().map(Column::name).collect(Collectors.joining(", ")))));
    }
    public boolean isEmpty() {
        return this.data.isEmpty();
    }
}
