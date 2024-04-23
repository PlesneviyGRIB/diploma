package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.exception.ValidationException;
import com.savchenko.sqlTool.model.command.domain.SimpleCommand;
import com.savchenko.sqlTool.model.complexity.SimpleEntry;
import com.savchenko.sqlTool.model.complexity.laziness.ClauseReducer;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.LazyTable;
import com.savchenko.sqlTool.model.resolver.CommandResult;

import java.util.Objects;

public class Limit implements SimpleCommand, ClauseReducer {

    private final Integer limit;

    public Limit(Integer limit) {
        this.limit = limit;
    }

    @Override
    public CommandResult run(LazyTable lazyTable, Projection projection) {

        if (limit < 0) {
            throw new ValidationException("Limit can not be less than 0! Current value is '%s'", limit);
        }

        return new CommandResult(
                new LazyTable(lazyTable.name(), lazyTable.columns(), lazyTable.dataStream().limit(limit), lazyTable.externalRow()),
                new SimpleEntry(this)
        );
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
