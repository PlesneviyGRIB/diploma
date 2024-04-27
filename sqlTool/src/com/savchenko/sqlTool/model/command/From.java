package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.command.domain.SimpleCommand;
import com.savchenko.sqlTool.model.domain.LazyTable;
import com.savchenko.sqlTool.model.domain.Projection;

import java.util.Objects;

public class From implements SimpleCommand {

    private final String tableName;

    public From(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public LazyTable run(LazyTable lazyTable, Projection projection) {

        var table = projection.getByName(tableName);

        return new LazyTable(table.name(), table.columns(), table.data().stream(), lazyTable.externalRow());
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        From from = (From) o;
        return Objects.equals(tableName, from.tableName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tableName);
    }
}
