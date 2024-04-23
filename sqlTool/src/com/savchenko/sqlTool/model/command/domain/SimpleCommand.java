package com.savchenko.sqlTool.model.command.domain;

import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.LazyTable;
import com.savchenko.sqlTool.model.resolver.CommandResult;

public interface SimpleCommand extends Command {

    CommandResult run(LazyTable lazyTable, Projection projection);

    @Override
    default <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
