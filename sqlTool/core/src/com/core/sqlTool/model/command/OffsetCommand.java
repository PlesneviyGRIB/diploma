package com.core.sqlTool.model.command;

import com.core.sqlTool.exception.ValidationException;
import com.core.sqlTool.model.command.domain.SimpleCommand;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.domain.Projection;

import java.util.Objects;

public record OffsetCommand(Integer offset) implements SimpleCommand {

    @Override
    public LazyTable run(LazyTable lazyTable, Projection projection) {

        if (offset < 0) {
            throw new ValidationException("Offset can not be less than 0! Current value is '%s'", offset);
        }

        return new LazyTable(lazyTable.name(), lazyTable.columns(), lazyTable.dataStream().skip(offset), lazyTable.externalRow());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OffsetCommand offset1 = (OffsetCommand) o;
        return Objects.equals(offset, offset1.offset);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(offset);
    }
}
