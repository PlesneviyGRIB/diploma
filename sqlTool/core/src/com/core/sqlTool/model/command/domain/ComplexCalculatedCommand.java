package com.core.sqlTool.model.command.domain;

import com.core.sqlTool.model.complexity.CalculatorEntry;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.domain.Projection;
import com.core.sqlTool.model.resolver.Resolver;

public interface ComplexCalculatedCommand extends Command {

    LazyTable run(LazyTable lazyTable, Projection projection, Resolver resolver, CalculatorEntry calculatorEntry);

    @Override
    default <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}

