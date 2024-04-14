package com.savchenko.sqlTool.model.command.domain;

import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.resolver.CommandResult;
import com.savchenko.sqlTool.model.resolver.Resolver;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComplexCalculedCommand that = (ComplexCalculedCommand) o;
        return Objects.equals(expression, that.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(expression);
    }
}

