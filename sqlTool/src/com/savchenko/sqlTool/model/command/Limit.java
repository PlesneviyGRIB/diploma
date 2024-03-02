package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.exception.ValidationException;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.model.domain.Projection;

import java.util.List;

import static java.lang.String.format;

public class Limit extends SimpleCommand {
    private final Integer limit;

    public Limit(Integer limit, Projection projection) {
        super(projection);
        this.limit = limit;
    }

    @Override
    public Table run(Table table) {
        var data = table.data();
        return new Table(table.name(), table.columns(), data.subList(0, Math.min(limit, data.size())), List.of());
    }

    @Override
    public void validate(Table table) {
        if(limit < 0) {
            throw new ValidationException("Limit can not be less than 0! Current value is '%s'", limit);
        }
    }
}
