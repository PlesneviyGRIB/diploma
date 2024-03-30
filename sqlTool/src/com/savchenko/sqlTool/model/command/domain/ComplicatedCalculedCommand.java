package com.savchenko.sqlTool.model.command.domain;

import com.savchenko.sqlTool.model.Resolver;
import com.savchenko.sqlTool.model.complexity.Calculator;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.expression.SubTable;
import com.savchenko.sqlTool.model.visitor.ExpressionTraversal;

import java.util.HashMap;
import java.util.Map;

public abstract class ComplicatedCalculedCommand implements Command {

    protected final Expression expression;

    protected final Projection projection;

    public ComplicatedCalculedCommand(Expression expression, Projection projection) {
        this.expression = expression;
        this.projection = projection;
    }

    public abstract Table run(Table table, Resolver resolver, Calculator calculator);

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    protected Map<SubTable, Table> calculateSubTables(Resolver resolver) {
        var calculatedSubTableMap = new HashMap<SubTable, Table>();

        expression.accept(new ExpressionTraversal() {
            @Override
            public Void visit(SubTable table) {
                calculatedSubTableMap.put(table, resolver.resolve(table.commands()));
                return null;
            }
        });

        return calculatedSubTableMap;
    }
}

