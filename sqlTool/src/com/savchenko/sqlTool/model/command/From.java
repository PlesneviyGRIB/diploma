package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.command.domain.SimpleCommand;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;

public class From implements SimpleCommand {
    private final String tableName;

    public From(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public Table run(Table table, Projection projection) {
        return projection.getByName(tableName);
    }
}
