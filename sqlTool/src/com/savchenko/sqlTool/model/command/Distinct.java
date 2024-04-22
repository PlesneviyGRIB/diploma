package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.command.domain.SimpleCalculedCommand;
import com.savchenko.sqlTool.model.complexity.SimpleCalculatorEntry;
import com.savchenko.sqlTool.model.complexity.laziness.Lazy;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.model.resolver.CommandResult;

import java.util.Objects;

public class Distinct implements SimpleCalculedCommand, Lazy {

    @Override
    public CommandResult run(Table table, Projection projection) {
        return new CommandResult(
                new Table(table.name(), table.columns(), table.dataStream().distinct(), table.externalRow()),
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
