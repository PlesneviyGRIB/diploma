package com.core.sqlTool.model.command;

import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.domain.Projection;

public record FromCommand(String tableName) implements SimpleCommand {

    @Override
    public LazyTable run(LazyTable lazyTable, Projection projection) {

        var table = projection.getTableByName(tableName);

        return new LazyTable(table.name(), table.columns(), table.data().stream(), lazyTable.externalRow());
    }

}
