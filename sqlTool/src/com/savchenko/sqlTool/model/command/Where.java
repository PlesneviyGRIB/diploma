package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.exception.UnsupportedTypeException;
import com.savchenko.sqlTool.model.expression.BooleanValue;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.expression.visitor.ExpressionCalculator;
import com.savchenko.sqlTool.model.expression.visitor.ExpressionValidator;
import com.savchenko.sqlTool.model.expression.visitor.ValueInjector;
import com.savchenko.sqlTool.model.structure.Table;
import com.savchenko.sqlTool.repository.Projection;

public class Where implements Command {
    private final Expression<?> expression;

    public Where(Expression<?> expression) {
        this.expression = expression;
    }

    @Override
    public Table run(Table table, Projection projection) {
        expression.accept(new ExpressionValidator(table.columns()));
        var data = table.data().stream()
                .filter(row -> {
                    var value = expression
                            .accept(new ValueInjector(table.columns(), row))
                            .accept(new ExpressionCalculator());
                    if(value instanceof BooleanValue bv){
                        return bv.value();
                    }
                    throw new UnsupportedTypeException();
                })
                .toList();
        return new Table(table.name(), table.columns(), data);
    }

    @Override
    public void validate(Table table, Projection projection) {
        this.expression.accept(new ExpressionValidator(table.columns()));
    }

}
