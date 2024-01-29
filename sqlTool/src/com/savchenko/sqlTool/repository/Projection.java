package com.savchenko.sqlTool.repository;

import com.savchenko.sqlTool.exception.TableNotFoundException;
import com.savchenko.sqlTool.model.structure.Table;

import java.util.List;

import static java.lang.String.format;

public record Projection(List<Table> tables) {
    public Table getByName(String tableName) {
        return tables.stream().filter(t -> t.name().equals(tableName)).findFirst()
                .orElseThrow(() -> new TableNotFoundException(tableName, this));
    }
}
