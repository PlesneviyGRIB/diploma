package com.savchenko.sqlTool.model.command.domain;

import com.savchenko.sqlTool.model.domain.LazyTable;
import com.savchenko.sqlTool.model.domain.Projection;

public interface SimpleCommand extends Command {

    LazyTable run(LazyTable lazyTable, Projection projection);

    @Override
    default <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
