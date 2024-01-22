package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.Table;
import com.savchenko.sqlTool.repository.Projection;

public class LeftJoin implements Command {
    @Override
    public Table run(Table table, Projection projection) {
        return null;
    }

    @Override
    public void validate(Projection projection) {
    }
}
