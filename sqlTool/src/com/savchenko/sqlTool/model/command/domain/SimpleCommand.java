package com.savchenko.sqlTool.model.command.domain;

import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;

public interface SimpleCommand extends Command {

    Table run(Table table, Projection projection);

    @Override
    default <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
