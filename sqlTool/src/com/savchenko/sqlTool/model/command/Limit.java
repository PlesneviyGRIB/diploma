package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.Table;
import com.savchenko.sqlTool.repository.Projection;

import static java.lang.String.format;

public class Limit implements Command {
    private final Integer limit;

    public Limit(Integer limit) {
        this.limit = limit;
    }

    @Override
    public Table run(Table table, Projection projection) {
        var data = table.data();
        return new Table(table.name(), table.columns(), data.subList(0, Math.min(limit, data.size())));
    }

    @Override
    public void validate(Projection projection) {
        if(limit < 0) {
            throw new RuntimeException(format("Limit can not be less than 0! Current value is '%s'", limit));
        }
    }
}
