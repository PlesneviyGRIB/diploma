package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.command.domain.SimpleCalculedCommand;
import com.savchenko.sqlTool.model.complexity.SimpleCalculatorEntry;
import com.savchenko.sqlTool.model.domain.LazyTable;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.resolver.CommandResult;

import java.util.Objects;

public class Distinct implements SimpleCalculedCommand {

    @Override
    public CommandResult run(LazyTable lazyTable, Projection projection) {
        return new CommandResult(
                new LazyTable(lazyTable.name(), lazyTable.columns(), lazyTable.dataStream().distinct(), lazyTable.externalRow()),
                new SimpleCalculatorEntry(this, 0)
        );
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
