package com.savchenko.sqlTool.model.operation;

import com.savchenko.sqlTool.model.Table;
import com.savchenko.sqlTool.repository.Projection;

public interface Operation {
    Table run(Table table, Projection projection);
}
