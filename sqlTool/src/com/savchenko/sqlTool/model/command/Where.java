package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.exception.UnsupportedTypeException;
import com.savchenko.sqlTool.model.command.domain.ComplexCalculedCommand;
import com.savchenko.sqlTool.model.complexity.ComplexCalculatorEntry;
import com.savchenko.sqlTool.model.complexity.laziness.Lazy;
import com.savchenko.sqlTool.model.domain.ExternalRow;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Row;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.model.expression.BooleanValue;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.expression.Value;
import com.savchenko.sqlTool.model.resolver.CommandResult;
import com.savchenko.sqlTool.model.resolver.Resolver;
import com.savchenko.sqlTool.model.visitor.*;
import com.savchenko.sqlTool.utils.ModelUtils;

import java.util.Optional;

public class Where extends ComplexCalculedCommand implements Lazy {

    public Where(Expression expression) {
        super(expression);
    }

    @Override
    public CommandResult run(Table table, Projection projection, Resolver resolver) {

        expression.accept(new ExpressionValidator(table.columns(), table.externalRow()));

        var isContextSensitiveExpression = expression
                .accept(new ContextSensitiveExpressionQualifier(table.columns()));

        Optional<Value<?>> valueProvider = isContextSensitiveExpression ?
                Optional.empty() : Optional.of(expression.accept(new ExpressionCalculator(resolver, ExternalRow.empty())));

        var calculatedExpressionEntry = expression.accept(new ExpressionComplexityCalculator(resolver, table.externalRow())).normalize();

        var dataStream = table.dataStream()
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
                });

        return new CommandResult(
                new Table(table.name(), table.columns(), dataStream, table.externalRow()),
                new ComplexCalculatorEntry(this, calculatedExpressionEntry, 0, isContextSensitiveExpression)
        );
    }

}
