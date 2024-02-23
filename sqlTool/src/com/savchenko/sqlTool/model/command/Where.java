package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.exception.UnsupportedTypeException;
import com.savchenko.sqlTool.model.expression.BooleanValue;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.expression.visitor.ExpressionCalculator;
import com.savchenko.sqlTool.model.expression.visitor.ExpressionValidator;
import com.savchenko.sqlTool.model.expression.visitor.ValueInjector;
import com.savchenko.sqlTool.model.structure.Table;
import com.savchenko.sqlTool.repository.Projection;

import java.util.List;

public class Where extends Command {
    private final Expression<?> expression;

    public Where(Expression<?> expression, Projection projection) {
        super(projection);
        this.expression = expression;
    }

    @Override
    public Table run(Table table) {
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
        return new Table(table.name(), table.columns(), data, List.of());
    }

    @Override
    public void validate(Table table) {
        this.expression.accept(new ExpressionValidator(table.columns()));
    }

}
