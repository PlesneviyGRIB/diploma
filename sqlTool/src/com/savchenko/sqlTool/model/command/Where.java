package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.exception.UnsupportedTypeException;
import com.savchenko.sqlTool.model.command.domain.ComplexCalculedCommand;
import com.savchenko.sqlTool.model.complexity.Calculator;
import com.savchenko.sqlTool.model.domain.ExternalRow;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.model.expression.BooleanValue;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.resolver.Resolver;
import com.savchenko.sqlTool.model.visitor.ExpressionCalculator;
import com.savchenko.sqlTool.model.visitor.ExpressionValidator;
import com.savchenko.sqlTool.model.visitor.ValueInjector;
import com.savchenko.sqlTool.utils.ModelUtils;

public class Where extends ComplexCalculedCommand {

    public Where(Expression expression) {
        super(expression);
    }

    @Override
    public Table run(Table table, Projection projection, Resolver resolver, Calculator calculator) {

        calculator.log(this, 0, 1);
        expression.accept(new ExpressionValidator(table.columns(), table.externalRow()));
        var data = table.data().stream()
                .filter(row -> {

                    var columnValue = ModelUtils.columnValueMap(table.columns(), row, table.externalRow());
                    var externalRow = table.externalRow().merge(new ExternalRow(table.columns(), row));

                    var value = expression
                            .accept(new ValueInjector(columnValue))
                            .accept(new ExpressionCalculator(resolver, externalRow));

                    if (value instanceof BooleanValue bv) {
                        return bv.value();
                    }

                    throw new UnsupportedTypeException();
                })
                .toList();
        return new Table(table.name(), table.columns(), data, table.externalRow());
    }

}
