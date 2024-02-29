package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.expression.SubTable;
import com.savchenko.sqlTool.model.structure.Table;
import com.savchenko.sqlTool.model.visitor.ExpressionTraversal;
import com.savchenko.sqlTool.query.QueryResolver;
import com.savchenko.sqlTool.repository.Projection;

import java.util.HashMap;
import java.util.Map;

public abstract class CalculatedCommand implements Command {

    protected final Expression expression;

    protected final Projection projection;

    public CalculatedCommand(Expression expression, Projection projection) {
        this.expression = expression;
        this.projection = projection;
    }

    public abstract Table run(Table table, QueryResolver resolver);

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    protected Map<SubTable, Table> calculateSubTables(QueryResolver resolver) {
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

