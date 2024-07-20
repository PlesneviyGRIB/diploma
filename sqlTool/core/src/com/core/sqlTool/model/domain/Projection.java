package com.core.sqlTool.model.domain;

import com.core.sqlTool.exception.TableNotFoundException;

import java.util.List;

public record Projection(List<Table> tables) {

    public Table getTableByName(String tableName) {
        return tables.stream()
                .filter(t -> t.name().equals(tableName))
                .findFirst()
                .orElseThrow(() -> new TableNotFoundException(tableName, this));
    }

}
