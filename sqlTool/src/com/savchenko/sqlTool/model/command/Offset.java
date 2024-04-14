package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.exception.ValidationException;
import com.savchenko.sqlTool.model.command.domain.SimpleCommand;
import com.savchenko.sqlTool.model.complexity.SimpleEntry;
import com.savchenko.sqlTool.model.complexity.laziness.ClauseReducer;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.model.resolver.CommandResult;

public class Offset implements SimpleCommand, ClauseReducer {
    private final Integer offset;

    public Offset(Integer offset) {
        this.offset = offset;
    }

    @Override
    public CommandResult run(Table table, Projection projection) {

        if (offset < 0) {
            throw new ValidationException("Offset can not be less than 0! Current value is '%s'", offset);
        }
        var data = table.data();

        return new CommandResult(
                new Table(table.name(), table.columns(), data.subList(Math.min(offset, data.size()), data.size()), table.externalRow()),
                new SimpleEntry(this)
        );
    }

}
