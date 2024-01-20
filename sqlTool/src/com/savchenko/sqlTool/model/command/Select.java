package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.Table;
import com.savchenko.sqlTool.repository.Projection;

import java.util.List;

public class Select implements Command {
    private final List<String> columns;

    public Select(List<String> columns) {
        this.columns = columns;
    }

    @Override
    public Table run(Table table, Projection projection) {
        return table;
    }
}
