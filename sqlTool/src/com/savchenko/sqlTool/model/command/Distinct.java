package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.command.domain.SimpleCalculedCommand;
import com.savchenko.sqlTool.model.complexity.SimpleCalculatorEntry;
import com.savchenko.sqlTool.model.complexity.laziness.Lazy;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.model.resolver.CommandResult;

public class Distinct implements SimpleCalculedCommand, Lazy {

    @Override
    public CommandResult run(Table table, Projection projection) {

        var data = table.data();
        var targetData = data.stream().distinct().toList();

        return new CommandResult(
                new Table(table.name(), table.columns(), targetData, table.externalRow()),
                new SimpleCalculatorEntry(this, data.size())
        );
    }
}
