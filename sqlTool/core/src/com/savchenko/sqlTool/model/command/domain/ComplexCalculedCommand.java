package com.savchenko.sqlTool.model.command.domain;

import com.savchenko.sqlTool.model.complexity.CalculatorEntry;
import com.savchenko.sqlTool.model.domain.LazyTable;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.resolver.Resolver;

import java.util.Objects;

public abstract class ComplexCalculedCommand implements Command {

    public final Expression expression;

    public ComplexCalculedCommand(Expression expression) {
        this.expression = expression;
    }

    public abstract LazyTable run(LazyTable lazyTable, Projection projection, Resolver resolver, CalculatorEntry calculatorEntry);

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

    public Expression getExpression() {
        return expression;
    }
}

