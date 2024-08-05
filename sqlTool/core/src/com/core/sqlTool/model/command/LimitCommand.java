package com.core.sqlTool.model.command;

import com.core.sqlTool.exception.InvalidLimitException;
import com.core.sqlTool.model.complexity.CalculatorEntry;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.domain.Projection;
import com.core.sqlTool.model.resolver.Resolver;

public record LimitCommand(Integer limit) implements Command {

    @Override
    public LazyTable run(LazyTable lazyTable, Projection projection, Resolver resolver, CalculatorEntry calculatorEntry) {

        if (limit < 0) {
            throw new InvalidLimitException(limit);
        }

        return new LazyTable(lazyTable.name(), lazyTable.columns(), lazyTable.dataStream().limit(limit), lazyTable.externalRow());
    }

    @Override
    public  <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}
