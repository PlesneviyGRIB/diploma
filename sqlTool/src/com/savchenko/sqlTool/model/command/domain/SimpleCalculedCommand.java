package com.savchenko.sqlTool.model.command.domain;

import com.savchenko.sqlTool.model.complexity.Calculator;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;

public abstract class SimpleCalculedCommand implements Command {

    protected final Projection projection;

    public SimpleCalculedCommand(Projection projection) {
        this.projection = projection;
    }

    public abstract Table run(Table table, Calculator calculator);

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
