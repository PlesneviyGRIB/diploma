package com.core.sqlTool.model.command.domain;

import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.domain.Projection;

public interface SimpleCommand extends Command {

    LazyTable run(LazyTable lazyTable, Projection projection);

    @Override
    default <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}
