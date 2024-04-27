package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.command.domain.SimpleCalculedCommand;
import com.savchenko.sqlTool.model.complexity.CalculatorEntry;
import com.savchenko.sqlTool.model.domain.LazyTable;
import com.savchenko.sqlTool.model.domain.Projection;

import java.util.Objects;

public class Distinct implements SimpleCalculedCommand {

    @Override
    public LazyTable run(LazyTable lazyTable, Projection projection, CalculatorEntry calculatorEntry) {
        return new LazyTable(lazyTable.name(), lazyTable.columns(), lazyTable.dataStream().peek(calculatorEntry::count).distinct(), lazyTable.externalRow());
    }

    @Override
    public boolean equals(Object o) {
        return o == this || !(o == null || getClass() != o.getClass());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(null);
    }

}
