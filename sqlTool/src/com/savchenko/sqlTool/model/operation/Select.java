package com.savchenko.sqlTool.model.operation;

import com.savchenko.sqlTool.model.Table;
import com.savchenko.sqlTool.repository.Projection;

import java.util.List;

public class Select implements Operation {
    private final List<String> columns;

    public Select(List<String> columns) {
        this.columns = columns;
    }

    @Override
    public Table run(Table table, Projection projection) {
        return table;
    }
}
