package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.exception.UnsupportedTypeException;
import com.savchenko.sqlTool.model.expression.BooleanValue;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.visitor.ExpressionCalculator;
import com.savchenko.sqlTool.model.visitor.ExpressionValidator;
import com.savchenko.sqlTool.model.visitor.ValueInjector;
import com.savchenko.sqlTool.model.structure.Table;
import com.savchenko.sqlTool.query.QueryResolver;
import com.savchenko.sqlTool.repository.Projection;

import java.util.List;
import java.util.Map;

public class Where extends CalculatedCommand {

    public Where(Expression expression, Projection projection) {
        super(expression, projection);
    }

    @Override
    public Table run(Table table, QueryResolver resolver) {
        expression.accept(new ExpressionValidator(table.columns()));
        var data = table.data().stream()
                .filter(row -> {

                    var subTables = calculateSubTables(resolver);

                    var value = expression
                            .accept(new ValueInjector(table.columns(), row, subTables))
                            .accept(new ExpressionCalculator());

                    if(value instanceof BooleanValue bv) {
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
