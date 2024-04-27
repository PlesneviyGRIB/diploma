package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.command.domain.ComplexCalculedCommand;
import com.savchenko.sqlTool.model.complexity.ComplexCalculatorEntry;
import com.savchenko.sqlTool.model.domain.*;
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

        ValidationUtils.expectBooleanValueAsResolvedType(expression, lazyTable.columns(), lazyTable.externalRow());

        var isContextSensitiveExpression = expression.accept(new ContextSensitiveExpressionQualifier(lazyTable.columns()));

        var valueProvider = isContextSensitiveExpression ?
                Optional.<BooleanValue>empty() :
                Optional.of((BooleanValue) expression.accept(new ExpressionCalculator(resolver, ExternalHeaderRow.empty())));

        var calculatedExpressionEntry = expression.accept(new ExpressionComplexityCalculator(resolver, lazyTable.externalRow())).normalize();

        Predicate<Row> predicate = row -> valueProvider.orElseGet(() -> {
            var externalRow = lazyTable.externalRow().merge(new ExternalHeaderRow(lazyTable.columns(), row.values()));
            return (BooleanValue) expression
                    .accept(new ValueInjector(new HeaderRow(lazyTable.columns(), row.values()), lazyTable.externalRow()))
                    .accept(new ExpressionCalculator(resolver, externalRow));
        }).value();

        return new CommandResult(
                new LazyTable(lazyTable.name(), lazyTable.columns(), lazyTable.dataStream().filter(predicate), lazyTable.externalRow()),
                new ComplexCalculatorEntry(this, calculatedExpressionEntry, 0, isContextSensitiveExpression)
        );
    }

}
