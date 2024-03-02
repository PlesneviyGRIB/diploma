package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.exception.UnsupportedTypeException;
import com.savchenko.sqlTool.model.expression.BooleanValue;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.visitor.ExpressionCalculator;
import com.savchenko.sqlTool.model.visitor.ExpressionValidator;
import com.savchenko.sqlTool.model.visitor.ValueInjector;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.model.Resolver;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.utils.ModelUtils;

import java.util.List;

public class Where extends CalculatedCommand {

    public Where(Expression expression, Projection projection) {
        super(expression, projection);
    }

    @Override
    public Table run(Table table, Resolver resolver) {
        expression.accept(new ExpressionValidator(table.columns()));
        var data = table.data().stream()
                .filter(row -> {

                    var subTables = calculateSubTables(resolver);
                    var columnValue = ModelUtils.columnValueMap(table.columns(), row);

                    var value = expression
                            .accept(new ValueInjector(columnValue, subTables))
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
