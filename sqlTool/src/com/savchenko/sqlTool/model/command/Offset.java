package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.exception.ValidationException;
import com.savchenko.sqlTool.model.structure.Table;
import com.savchenko.sqlTool.repository.Projection;

public class Offset implements Command {
    private final Integer offset;

    public Offset(Integer offset) {
        this.offset = offset;
    }

    @Override
    public Table run(Table table, Projection projection) {
        var data = table.data();
        return new Table(table.name(), table.columns(), data.subList(Math.min(offset, data.size()), data.size()));
    }

    @Override
    public void validate(Table table, Projection projection) {
        if(offset < 0) {
            throw new ValidationException("Offset can not be less than 0! Current value is '%s'", offset);
        }
    }
}
