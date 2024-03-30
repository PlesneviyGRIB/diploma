package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.command.domain.SimpleCalculedCommand;
import com.savchenko.sqlTool.model.complexity.Calculator;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;

import java.util.List;

public class Distinct extends SimpleCalculedCommand {
    public Distinct(Projection projection) {
        super(projection);
    }

    @Override
    public Table run(Table table, Calculator calculator) {
        var data = table.data().stream().distinct().toList();
        return new Table(table.name(), table.columns(), data, List.of());
    }
}
