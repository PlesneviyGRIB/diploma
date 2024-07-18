package com.core.sqlTool.model.command;

import com.core.sqlTool.model.command.domain.SimpleCommand;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.domain.Projection;

import java.util.Objects;

public record FromCommand(String tableName) implements SimpleCommand {

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
        FromCommand from = (FromCommand) o;
        return Objects.equals(tableName, from.tableName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tableName);
    }
}
