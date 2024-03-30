package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.command.domain.SimpleCalculedCommand;
import com.savchenko.sqlTool.model.complexity.Calculator;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;

import java.util.List;

public class Distinct implements SimpleCalculedCommand {

    @Override
    public Table run(Table table, Projection projection, Calculator calculator) {
        var data = table.data().stream().distinct().toList();
        return new Table(table.name(), table.columns(), data, List.of());
    }
}
