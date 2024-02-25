package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.index.Index;
import com.savchenko.sqlTool.model.structure.Table;
import com.savchenko.sqlTool.repository.Projection;
import com.savchenko.sqlTool.utils.ModelUtils;

public class ConstructIndex extends SimpleCommand {

    private final Index index;

    public ConstructIndex(Index index, Projection projection) {
        super(projection);
        this.index = index;
    }

    @Override
    public Table run(Table table) {
        return null;
    }

    @Override
    public void validate(Table table) {
        index.getColumns().forEach(column -> ModelUtils.resolveColumn(table.columns(), column));
    }
}