package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.Table;
import com.savchenko.sqlTool.repository.Projection;

public interface Command {
    Table run(Table table, Projection projection);
    void validate(Projection projection);
}
