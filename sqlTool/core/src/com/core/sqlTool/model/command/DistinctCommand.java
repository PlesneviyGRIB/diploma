package com.core.sqlTool.model.command;

import com.core.sqlTool.model.command.domain.SimpleCalculedCommand;
import com.core.sqlTool.model.complexity.CalculatorEntry;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.domain.Projection;

public record DistinctCommand() implements SimpleCalculedCommand {

    @Override
    public LazyTable run(LazyTable lazyTable, Projection projection, CalculatorEntry calculatorEntry) {
        return new LazyTable(lazyTable.name(), lazyTable.columns(), lazyTable.dataStream().peek(calculatorEntry::count).distinct(), lazyTable.externalRow());
    }

}
