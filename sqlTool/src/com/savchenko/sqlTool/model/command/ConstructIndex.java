package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.command.domain.SimpleCalculedCommand;
import com.savchenko.sqlTool.model.complexity.laziness.LazinessIndependent;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.model.index.Index;
import com.savchenko.sqlTool.model.resolver.CommandResult;
import com.savchenko.sqlTool.utils.ModelUtils;

public class ConstructIndex implements SimpleCalculedCommand, LazinessIndependent {

    private final Index index;

    public ConstructIndex(Index index) {
        this.index = index;
    }

    @Override
    public CommandResult run(Table table, Projection projection) {

        index.getColumns().forEach(column -> ModelUtils.resolveColumn(table.columns(), column));

        return null;
    }

}
