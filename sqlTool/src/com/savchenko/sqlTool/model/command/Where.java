package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.expression.ExpressionToPredicateVisitor;
import com.savchenko.sqlTool.model.expression.ExpressionValidationVisitor;
import com.savchenko.sqlTool.model.structure.Table;
import com.savchenko.sqlTool.repository.Projection;

import java.util.List;

public class Where implements Command {
    private final Expression<?> expression;

    public Where(Expression<?> expression) {
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

    @Override
    public void validate(Table table, Projection projection) {
        this.expression.accept(new ExpressionValidationVisitor(table.columns()));
    }

}
