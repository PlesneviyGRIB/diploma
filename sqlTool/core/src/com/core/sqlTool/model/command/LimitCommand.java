package com.core.sqlTool.model.command;

import com.core.sqlTool.exception.ValidationException;
import com.core.sqlTool.model.command.domain.SimpleCommand;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.domain.Projection;

import java.util.Objects;

public record LimitCommand(Integer limit) implements SimpleCommand {

    @Override
    public LazyTable run(LazyTable lazyTable, Projection projection) {

        if (limit < 0) {
            throw new ValidationException("Limit can not be less than 0! Current value is '%s'", limit);
        }

        return new LazyTable(lazyTable.name(), lazyTable.columns(), lazyTable.dataStream().limit(limit), lazyTable.externalRow());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LimitCommand limit1 = (LimitCommand) o;
        return Objects.equals(limit, limit1.limit);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(limit);
    }
}
