package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.exception.ValidationException;
import com.savchenko.sqlTool.model.command.domain.SimpleCommand;
import com.savchenko.sqlTool.model.complexity.Calculator;
import com.savchenko.sqlTool.model.complexity.laziness.LazinessIndependent;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.utils.ModelUtils;

public class Alias implements SimpleCommand, LazinessIndependent {

    private final String alias;

    public Alias(String alias) {
        this.alias = alias;
    }

    @Override
    public Table run(Table table, Projection projection, Calculator calculator) {
        calculator.log(this);
        projection.tables().stream()
                .filter(t -> t.name().equals(alias))
                .findFirst()
                .ifPresent(t -> {
                    throw new ValidationException("Wrong alias '%s'. There are table with such name in database.", alias);
                });

        return ModelUtils.renameTable(table, alias);
    }

}
