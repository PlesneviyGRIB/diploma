package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.exception.ValidationException;
import com.savchenko.sqlTool.model.command.domain.SimpleCommand;
import com.savchenko.sqlTool.model.domain.LazyTable;
import com.savchenko.sqlTool.model.domain.Projection;

import java.util.Objects;

public class Limit implements SimpleCommand {

    private final Integer limit;

    public Limit(Integer limit) {
        this.limit = limit;
    }

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
        Limit limit1 = (Limit) o;
        return Objects.equals(limit, limit1.limit);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(limit);
    }
}
