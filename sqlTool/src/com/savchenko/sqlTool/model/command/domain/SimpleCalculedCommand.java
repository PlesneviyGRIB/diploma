package com.savchenko.sqlTool.model.command.domain;

import com.savchenko.sqlTool.model.complexity.Calculator;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;

public interface SimpleCalculedCommand extends Command {

    Table run(Table table, Projection projection, Calculator calculator);

    @Override
    default <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
