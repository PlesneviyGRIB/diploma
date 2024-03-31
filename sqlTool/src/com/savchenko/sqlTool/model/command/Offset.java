package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.exception.ValidationException;
import com.savchenko.sqlTool.model.command.domain.SimpleCommand;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;

public class Offset implements SimpleCommand {
    private final Integer offset;

    public Offset(Integer offset) {
        this.offset = offset;
    }

    @Override
    public Table run(Table table, Projection projection) {
        if (offset < 0) {
            throw new ValidationException("Offset can not be less than 0! Current value is '%s'", offset);
        }
        var data = table.data();
        return new Table(table.name(), table.columns(), data.subList(Math.min(offset, data.size()), data.size()), table.externalRow());
    }

}
