package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.exception.ValidationException;
import com.savchenko.sqlTool.model.command.domain.SimpleCommand;
import com.savchenko.sqlTool.model.complexity.laziness.ClauseReducer;
import com.savchenko.sqlTool.model.complexity.Calculator;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;

public class Limit implements SimpleCommand, ClauseReducer {
    private final Integer limit;

    public Limit(Integer limit) {
        this.limit = limit;
    }

    @Override
    public Table run(Table table, Projection projection, Calculator calculator) {
        if (limit < 0) {
            throw new ValidationException("Limit can not be less than 0! Current value is '%s'", limit);
        }

        calculator.log(this);
        var data = table.data();
        return new Table(table.name(), table.columns(), data.subList(0, Math.min(limit, data.size())), table.externalRow());
    }

}
