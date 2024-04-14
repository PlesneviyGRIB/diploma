package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.command.domain.SimpleCommand;
import com.savchenko.sqlTool.model.complexity.SimpleEntry;
import com.savchenko.sqlTool.model.complexity.laziness.Lazy;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.model.resolver.CommandResult;

import java.util.Objects;

public class From implements SimpleCommand, Lazy {
    private final String tableName;

    public From(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public CommandResult run(Table table, Projection projection) {

        var resolvedTable = projection.getByName(tableName);

        return new CommandResult(
                new Table(resolvedTable.name(), resolvedTable.columns(), resolvedTable.data(), table.externalRow()),
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
