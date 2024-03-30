package com.savchenko.sqlTool.model.command.domain;

import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;

public abstract class SimpleCommand implements Command {

    protected final Projection projection;

    public SimpleCommand(Projection projection) {
        this.projection = projection;
    }

    public abstract Table run(Table table);

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
