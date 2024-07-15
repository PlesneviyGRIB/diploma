package com.core.sqlTool.model.domain;

import com.core.sqlTool.exception.TableNotFoundException;

import java.util.List;

import static java.lang.String.format;

public record Projection(List<Table> tables) {

    public Table getByName(String tableName) {
        return tables.stream().filter(t -> t.name().equals(tableName)).findFirst()
                .orElseThrow(() -> new TableNotFoundException(tableName, this));
    }

}
