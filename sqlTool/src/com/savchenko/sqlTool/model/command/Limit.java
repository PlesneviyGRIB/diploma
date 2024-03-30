package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.exception.ValidationException;
import com.savchenko.sqlTool.model.command.domain.SimpleCommand;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;

import java.util.List;

public class Limit implements SimpleCommand {
    private final Integer limit;

    public Limit(Integer limit) {
        this.limit = limit;
    }

    @Override
    public Table run(Table table, Projection projection) {
        if (limit < 0) {
            throw new ValidationException("Limit can not be less than 0! Current value is '%s'", limit);
        }
        var data = table.data();
        return new Table(table.name(), table.columns(), data.subList(0, Math.min(limit, data.size())), List.of());
    }

}
