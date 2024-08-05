package com.core.sqlTool.model.command;

import com.core.sqlTool.model.complexity.CalculatorEntry;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.domain.Projection;
import com.core.sqlTool.model.resolver.Resolver;

public record DistinctCommand() implements Command {

    @Override
    public LazyTable run(LazyTable lazyTable, Projection projection, Resolver resolver, CalculatorEntry calculatorEntry) {
        return new LazyTable(lazyTable.name(), lazyTable.columns(), lazyTable.dataStream().peek(calculatorEntry::count).distinct(), lazyTable.externalRow());
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}
