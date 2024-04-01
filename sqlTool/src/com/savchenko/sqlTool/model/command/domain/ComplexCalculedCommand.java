package com.savchenko.sqlTool.model.command.domain;

import com.savchenko.sqlTool.model.resolver.Resolver;
import com.savchenko.sqlTool.model.complexity.Calculator;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.model.expression.Expression;

public abstract class ComplexCalculedCommand implements Command {

    protected final Expression expression;

    public ComplexCalculedCommand(Expression expression) {
        this.expression = expression;
    }

    public abstract Table run(Table table, Projection projection, Resolver resolver, Calculator calculator);

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}

