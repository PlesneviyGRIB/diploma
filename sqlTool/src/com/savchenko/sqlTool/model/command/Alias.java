package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.exception.ValidationException;
import com.savchenko.sqlTool.model.command.domain.SimpleCommand;
import com.savchenko.sqlTool.model.complexity.SimpleEntry;
import com.savchenko.sqlTool.model.complexity.laziness.LazinessIndependent;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.model.resolver.CommandResult;
import com.savchenko.sqlTool.utils.ModelUtils;

import java.util.Objects;

public class Alias implements SimpleCommand, LazinessIndependent {

    private final String alias;

    public Alias(String alias) {
        this.alias = alias;
    }

    @Override
    public CommandResult run(Table table, Projection projection) {

        projection.tables().stream()
                .filter(t -> t.name().equals(alias))
                .findFirst()
                .ifPresent(t -> {
                    throw new ValidationException("Wrong alias '%s'. There are table with such name in database.", alias);
                });

        return new CommandResult(
                ModelUtils.renameTable(table, alias),
                new SimpleEntry(this)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Alias alias1 = (Alias) o;
        return Objects.equals(alias, alias1.alias);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(alias);
    }
}
