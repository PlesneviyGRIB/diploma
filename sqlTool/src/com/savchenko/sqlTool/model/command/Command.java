package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.structure.Table;
import com.savchenko.sqlTool.repository.Projection;

public interface Command {
    Table run(Table table, Projection projection);
    default void validate(Table table, Projection projection) {};
}
