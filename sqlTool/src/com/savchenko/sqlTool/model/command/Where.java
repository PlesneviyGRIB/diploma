package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.exception.UnsupportedTypeException;
import com.savchenko.sqlTool.model.command.domain.ComplexCalculedCommand;
import com.savchenko.sqlTool.model.complexity.Calculator;
import com.savchenko.sqlTool.model.domain.ExternalRow;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Row;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.model.expression.BooleanValue;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.expression.Value;
import com.savchenko.sqlTool.model.resolver.Resolver;
import com.savchenko.sqlTool.model.visitor.*;
import com.savchenko.sqlTool.utils.ModelUtils;

import java.util.Optional;

public class Where extends ComplexCalculedCommand {

    public Where(Expression expression) {
        super(expression);
    }

    @Override
    public Table run(Table table, Projection projection, Resolver resolver, Calculator calculator) {

        expression.accept(new ExpressionValidator(table.columns(), table.externalRow()));

        var isContextSensitiveExpression = expression
                .accept(new ContextSensitiveExpressionQualifier(resolver, ModelUtils.getFullCopyExternalRow(table)));

        Optional<Value<?>> valueProvider = isContextSensitiveExpression ?
                Optional.empty() : Optional.of(expression.accept(new ExpressionCalculator(resolver, ExternalRow.empty())));

        var calculedExpressionEntry = expression.accept(new ExpressionComplexityCalculator(resolver, ModelUtils.getFullCopyExternalRow(table))).normalize();

        calculator.log(this, calculedExpressionEntry, table.data().size(), isContextSensitiveExpression);

        var data = table.data().stream()
                .filter(row -> {

                    var value = valueProvider.orElseGet(() -> {
                        var externalRow = table.externalRow().merge(new ExternalRow(table.columns(), row));
                        return expression
                                .accept(new ValueInjector(new Row(table.columns(), row), table.externalRow()))
                                .accept(new ExpressionCalculator(resolver, externalRow));
                    });

                    if (value instanceof BooleanValue bv) {
                        return bv.value();
                    }

                    throw new UnsupportedTypeException();
                })
                .toList();
        return new Table(table.name(), table.columns(), data, table.externalRow());
    }

}
