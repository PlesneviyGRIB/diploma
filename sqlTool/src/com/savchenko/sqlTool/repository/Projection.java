package com.savchenko.sqlTool.repository;

import com.savchenko.sqlTool.model.Table;

import java.util.List;

import static java.lang.String.format;

public record Projection(List<Table> tables) {
    public Table getByName(String tableName) {
        return tables.stream().filter(t -> t.name().equals(tableName)).findFirst()
                .orElseThrow(() -> new RuntimeException(format("Unable to find table '%s'", tableName)));
    }
}
