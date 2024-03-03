package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;

import java.util.List;

public class Distinct extends SimpleCommand {
    public Distinct(Projection projection) {
        super(projection);
    }

    @Override
    public Table run(Table table) {
        var data = table.data().stream().distinct().toList();
        return new Table(table.name(), table.columns(), data, List.of());
    }
}
