package com.core.sqlTool.model.command;

import com.core.sqlTool.exception.InvalidOffsetException;
import com.core.sqlTool.model.complexity.CalculatorEntry;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.domain.Projection;
import com.core.sqlTool.model.resolver.Resolver;

public record OffsetCommand(Integer offset) implements Command {

    @Override
    public LazyTable run(LazyTable lazyTable, Projection projection, Resolver resolver, CalculatorEntry calculatorEntry) {

        if (offset < 0) {
            throw new InvalidOffsetException(offset);
        }

        return new LazyTable(lazyTable.name(), lazyTable.columns(), lazyTable.dataStream().skip(offset), lazyTable.externalRow());
    }

    @Override
    public  <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}
