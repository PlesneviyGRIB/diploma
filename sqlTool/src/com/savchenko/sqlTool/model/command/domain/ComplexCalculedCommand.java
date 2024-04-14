package com.savchenko.sqlTool.model.command.domain;

import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.resolver.CommandResult;
import com.savchenko.sqlTool.model.resolver.Resolver;

public abstract class ComplexCalculedCommand implements Command {

    protected final Expression expression;

    public ComplexCalculedCommand(Expression expression) {
        this.expression = expression;
    }

    public abstract CommandResult run(Table table, Projection projection, Resolver resolver);

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}

