package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.exception.ValidationException;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.utils.ModelUtils;

public class Alias extends SimpleCommand {
    private final String alias;
    public Alias(String alias, Projection projection) {
        super(projection);
        this.alias = alias;
    }

    @Override
    public Table run(Table table) {
        projection.tables().stream()
                .filter(t -> t.name().equals(alias))
                .findFirst()
                .ifPresent(t -> { throw new ValidationException("Wrong alias '%s'. There are table with such name in database.", alias); });

        return ModelUtils.renameTable(table, alias);
    }

}
