package com.core.sqlTool.model.command;

import com.core.sqlTool.model.command.domain.SimpleCommand;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.domain.Projection;
import com.core.sqlTool.utils.ModelUtils;

public record TableAliasCommand(String alias) implements SimpleCommand {

    @Override
    public LazyTable run(LazyTable lazyTable, Projection projection) {
        return ModelUtils.renameTable(lazyTable, alias);
    }

}
