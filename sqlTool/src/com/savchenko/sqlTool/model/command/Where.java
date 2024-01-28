package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.operation.Expression;
import com.savchenko.sqlTool.model.operation.ExpressionToPredicateVisitor;
import com.savchenko.sqlTool.model.structure.Table;
import com.savchenko.sqlTool.model.predicate.Predicate;
import com.savchenko.sqlTool.repository.Projection;

import java.util.List;

public class Where implements Command {
    private final Expression expression;

    public Where(Expression expression) {
        this.expression = expression;
    }

    @Override
    public Table run(Table table, Projection projection) {
        var predicate = expression.accept(new ExpressionToPredicateVisitor());
        var data = table.data().stream()
                .filter(row -> predicate.test(table.columns(), row))
                .toList();
        return new Table(table.name(), table.columns(), data);
    }

}
