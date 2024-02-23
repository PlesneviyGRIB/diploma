package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.structure.Table;
import com.savchenko.sqlTool.repository.Projection;

public abstract class Command {

    protected final Projection projection;

    public Command(Projection projection) {
        this.projection = projection;
    }
    public abstract Table run(Table table);
    public void validate(Table table) {};
}
