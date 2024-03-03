package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.index.Index;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.utils.ModelUtils;

public class ConstructIndex extends SimpleCommand {

    private final Index index;

    public ConstructIndex(Index index, Projection projection) {
        super(projection);
        this.index = index;
    }

    @Override
    public Table run(Table table) {
        index.getColumns().forEach(column -> ModelUtils.resolveColumn(table.columns(), column));
        return null;
    }

}
