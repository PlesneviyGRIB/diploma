package com.core.sqlTool.model.command;

import com.core.sqlTool.exception.ValidationException;
import com.core.sqlTool.model.command.domain.SimpleCommand;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.domain.Projection;
import com.core.sqlTool.utils.ModelUtils;

public record AliasCommand(String alias) implements SimpleCommand {

    @Override
    public LazyTable run(LazyTable lazyTable, Projection projection) {

        projection.tables().stream()
                .filter(t -> t.name().equals(alias))
                .findFirst()
                .ifPresent(t -> {
                    throw new ValidationException("Wrong alias '%s'. There are tableName with such columnName in database.", alias);
                });

        return ModelUtils.renameTable(lazyTable, alias);
    }

}
