package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.command.domain.SimpleCommand;
import com.savchenko.sqlTool.model.complexity.Calculator;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;

public class From implements SimpleCommand {
    private final String tableName;

    public From(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public Table run(Table table, Projection projection, Calculator calculator) {

        calculator.log(this);

        var resolvedTable = projection.getByName(tableName);

        return new Table(resolvedTable.name(), resolvedTable.columns(), resolvedTable.data(), table.externalRow());
    }
}
