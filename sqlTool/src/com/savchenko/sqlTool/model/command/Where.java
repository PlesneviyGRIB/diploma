package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.expression.visitor.ExpressionCalculator;
import com.savchenko.sqlTool.model.expression.visitor.ExpressionValidator;
import com.savchenko.sqlTool.model.structure.Table;
import com.savchenko.sqlTool.repository.Projection;

public class Where implements Command {
    private final Expression<?> expression;

    public Where(Expression<?> expression) {
        this.expression = expression;
    }

    @Override
    public Table run(Table table, Projection projection) {
        var predicate = expression.accept(new ExpressionCalculator());
        var data = table.data().stream()
                .filter(row -> predicate.test(table.columns(), row))
                .toList();
        return new Table(table.name(), table.columns(), data);
    }

    @Override
    public void validate(Table table, Projection projection) {
        this.expression.accept(new ExpressionValidator(table.columns()));
    }

}
