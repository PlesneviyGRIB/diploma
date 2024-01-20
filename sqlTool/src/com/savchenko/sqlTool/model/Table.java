package com.savchenko.sqlTool.model;

import java.util.List;

import static java.lang.String.format;

public record Table(String name, List<Column> columns, List<List<Comparable<?>>> data) {
    public Column getColumnByName(String name) {
        return columns.stream().filter(c -> c.name().equals(name)).findFirst()
                .orElseThrow(() -> new RuntimeException(format("Unable to find column '%s' in table '%s'", name, this.name)));
    };
    public boolean isEmpty() {
        return this.data.isEmpty();
    }
}
