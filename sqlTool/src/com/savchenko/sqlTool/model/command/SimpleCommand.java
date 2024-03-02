package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.model.domain.Projection;

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
