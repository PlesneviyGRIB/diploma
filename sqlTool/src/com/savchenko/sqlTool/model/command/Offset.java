package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.exception.ValidationException;
import com.savchenko.sqlTool.model.command.domain.SimpleCommand;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;

import java.util.List;

public class Offset extends SimpleCommand {
    private final Integer offset;

    public Offset(Integer offset, Projection projection) {
        super(projection);
        this.offset = offset;
    }

    @Override
    public Table run(Table table) {
        if (offset < 0) {
            throw new ValidationException("Offset can not be less than 0! Current value is '%s'", offset);
        }
        var data = table.data();
        return new Table(table.name(), table.columns(), data.subList(Math.min(offset, data.size()), data.size()), List.of());
    }

}
