package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.model.domain.Projection;

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
