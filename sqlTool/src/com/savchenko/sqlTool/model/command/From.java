package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.command.domain.SimpleCommand;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;

public class From extends SimpleCommand {
    private final String tableName;

    public From(String tableName, Projection projection) {
        super(projection);
        this.tableName = tableName;
    }

    @Override
    public Table run(Table table) {
        return projection.getByName(tableName);
    }
}
