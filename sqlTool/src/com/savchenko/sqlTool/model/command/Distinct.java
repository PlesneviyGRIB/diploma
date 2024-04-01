package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.command.domain.SimpleCalculedCommand;
import com.savchenko.sqlTool.model.complexity.Calculator;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;

public class Distinct implements SimpleCalculedCommand {

    @Override
    public Table run(Table table, Projection projection, Calculator calculator) {

        var data = table.data();
        var targetData = data.stream().distinct().toList();

        calculator.log(this, data.size());

        return new Table(table.name(), table.columns(), targetData, table.externalRow());
    }
}
