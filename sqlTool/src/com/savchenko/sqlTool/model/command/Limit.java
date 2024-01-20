package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.Table;
import com.savchenko.sqlTool.repository.Projection;

public class Limit implements Command {
    private final Integer limit;

    public Limit(Integer limit) {
        this.limit = limit;
    }

    @Override
    public Table run(Table table, Projection projection) {
        var data = table.data();
        return new Table(table.name(), table.columns(), data.subList(0, limit));
    }
}
