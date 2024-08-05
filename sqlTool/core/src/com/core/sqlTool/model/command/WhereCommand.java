package com.core.sqlTool.model.command;

import com.core.sqlTool.model.complexity.CalculatorEntry;
import com.core.sqlTool.model.domain.HeaderRow;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.domain.Projection;
import com.core.sqlTool.model.domain.Row;
import com.core.sqlTool.model.expression.BooleanValue;
import com.core.sqlTool.model.expression.Expression;
import com.core.sqlTool.model.resolver.Resolver;
import com.core.sqlTool.model.visitor.ContextSensitiveExpressionQualifier;
import com.core.sqlTool.model.visitor.ExpressionCalculator;
import com.core.sqlTool.utils.ValidationUtils;

import java.util.Optional;
import java.util.function.Predicate;

public record WhereCommand(Expression expression) implements Command {

    @Override
    public LazyTable run(LazyTable lazyTable, Projection projection, Resolver resolver, CalculatorEntry calculatorEntry) {

        var externalRow = lazyTable.externalRow();
        var columns = lazyTable.columns();

        ValidationUtils.expectBooleanValueAsResolvedType(expression, columns, externalRow);

        var isContextSensitiveExpression = expression.accept(new ContextSensitiveExpressionQualifier(columns));

        var valueProvider = isContextSensitiveExpression ?
                Optional.<BooleanValue>empty() :
                Optional.of((BooleanValue) expression.accept(new ExpressionCalculator(resolver, HeaderRow.empty(), externalRow)).getValue());

        Predicate<Row> predicate = row -> {
            var headerRow = new HeaderRow(columns, row);
            return valueProvider.orElseGet(
                    () -> (BooleanValue) expression.accept(new ExpressionCalculator(resolver, headerRow, externalRow)).getValue()
            ).value();
        };

        return new LazyTable(lazyTable.name(), columns, lazyTable.dataStream().filter(predicate), externalRow);
    }

    @Override
    public  <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}
