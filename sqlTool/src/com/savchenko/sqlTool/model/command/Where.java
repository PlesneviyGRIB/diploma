package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.command.domain.ComplexCalculedCommand;
import com.savchenko.sqlTool.model.complexity.ComplexCalculatorEntry;
import com.savchenko.sqlTool.model.domain.HeaderRow;
import com.savchenko.sqlTool.model.domain.LazyTable;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Row;
import com.savchenko.sqlTool.model.expression.BooleanValue;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.resolver.CommandResult;
import com.savchenko.sqlTool.model.resolver.Resolver;
import com.savchenko.sqlTool.model.visitor.ContextSensitiveExpressionQualifier;
import com.savchenko.sqlTool.model.visitor.ExpressionCalculator;
import com.savchenko.sqlTool.model.visitor.ExpressionComplexityCalculator;
import com.savchenko.sqlTool.model.visitor.ValueInjector;
import com.savchenko.sqlTool.utils.ValidationUtils;

import java.util.Optional;
import java.util.function.Predicate;

public class Where extends ComplexCalculedCommand {

    public Where(Expression expression) {
        super(expression);
    }

    @Override
    public CommandResult run(LazyTable lazyTable, Projection projection, Resolver resolver) {

        var externalRow = lazyTable.externalRow();
        var columns = lazyTable.columns();

        ValidationUtils.expectBooleanValueAsResolvedType(expression, columns, externalRow);

        var isContextSensitiveExpression = expression.accept(new ContextSensitiveExpressionQualifier(columns));

        var valueProvider = isContextSensitiveExpression ?
                Optional.<BooleanValue>empty() :
                Optional.of((BooleanValue) expression.accept(new ExpressionCalculator(resolver, HeaderRow.empty(), externalRow)));

        var calculatedExpressionEntry = expression
                .accept(new ExpressionComplexityCalculator(resolver, lazyTable.phonyHeaderRow(), externalRow))
                .normalize();

        Predicate<Row> predicate = row -> {
            var headerRow = new HeaderRow(columns, row);
            return valueProvider.orElseGet(
                    () -> (BooleanValue) expression
                            .accept(new ValueInjector(headerRow, externalRow))
                            .accept(new ExpressionCalculator(resolver, headerRow, externalRow))
            ).value();
        };

        return new CommandResult(
                new LazyTable(lazyTable.name(), columns, lazyTable.dataStream().filter(predicate), externalRow),
                new ComplexCalculatorEntry(this, calculatedExpressionEntry, 0, isContextSensitiveExpression)
        );
    }

}
