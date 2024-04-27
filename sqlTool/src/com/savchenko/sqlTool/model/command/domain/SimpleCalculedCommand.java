package com.savchenko.sqlTool.model.command.domain;

import com.savchenko.sqlTool.model.complexity.CalculatorEntry;
import com.savchenko.sqlTool.model.domain.LazyTable;
import com.savchenko.sqlTool.model.domain.Projection;

public interface SimpleCalculedCommand extends Command {

    LazyTable run(LazyTable lazyTable, Projection projection, CalculatorEntry entry);

    @Override
    default <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
