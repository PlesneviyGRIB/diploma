package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.structure.Table;
import com.savchenko.sqlTool.query.QueryResolver;
import com.savchenko.sqlTool.repository.Projection;

public abstract class CalculatedCommand implements Command {

    protected final Projection projection;

    public CalculatedCommand(Projection projection) {
        this.projection = projection;
    }

    public abstract Table run(Table table, QueryResolver resolver);

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}

