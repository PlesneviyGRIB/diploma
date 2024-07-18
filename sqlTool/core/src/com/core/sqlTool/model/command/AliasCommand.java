package com.core.sqlTool.model.command;

import com.core.sqlTool.model.command.domain.SimpleCommand;
import com.core.sqlTool.exception.ValidationException;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.domain.Projection;
import com.core.sqlTool.utils.ModelUtils;

import java.util.Objects;

public class AliasCommand implements SimpleCommand {

    private final String alias;

    public AliasCommand(String alias) {
        this.alias = alias;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AliasCommand alias1 = (AliasCommand) o;
        return Objects.equals(alias, alias1.alias);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(alias);
    }

    public String getAlias() {
        return alias;
    }

}
