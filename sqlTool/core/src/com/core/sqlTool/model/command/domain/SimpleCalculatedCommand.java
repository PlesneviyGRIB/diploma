package com.core.sqlTool.model.command.domain;

import com.core.sqlTool.model.complexity.CalculatorEntry;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.domain.Projection;

public interface SimpleCalculatedCommand extends Command {

    LazyTable run(LazyTable lazyTable, Projection projection, CalculatorEntry entry);

    @Override
    default <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}
