package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.command.domain.SimpleCommand;
import com.savchenko.sqlTool.model.complexity.SimpleEntry;
import com.savchenko.sqlTool.model.complexity.laziness.Lazy;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.LazyTable;
import com.savchenko.sqlTool.model.resolver.CommandResult;

import java.util.Objects;

public class From implements SimpleCommand, Lazy {
    private final String tableName;

    public From(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public CommandResult run(LazyTable lazyTable, Projection projection) {

        var table = projection.getByName(tableName);

        return new CommandResult(
                new LazyTable(table.name(), table.columns(), table.data().stream(), lazyTable.externalRow()),
                new SimpleEntry(this)
        );
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
