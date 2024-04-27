package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.exception.ValidationException;
import com.savchenko.sqlTool.model.command.domain.SimpleCommand;
import com.savchenko.sqlTool.model.complexity.SimpleEntry;
import com.savchenko.sqlTool.model.domain.LazyTable;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.resolver.CommandResult;

import java.util.Objects;

public class Offset implements SimpleCommand {
    private final Integer offset;

    public Offset(Integer offset) {
        this.offset = offset;
    }

    @Override
    public CommandResult run(LazyTable lazyTable, Projection projection) {

        if (offset < 0) {
            throw new ValidationException("Offset can not be less than 0! Current value is '%s'", offset);
        }

        return new CommandResult(
                new LazyTable(lazyTable.name(), lazyTable.columns(), lazyTable.dataStream().skip(offset), lazyTable.externalRow()),
                new SimpleEntry(this)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Offset offset1 = (Offset) o;
        return Objects.equals(offset, offset1.offset);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(offset);
    }
}
