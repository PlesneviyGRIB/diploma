package com.core.sqlTool.model.command;

import com.core.sqlTool.model.complexity.CalculatorEntry;
import com.core.sqlTool.model.domain.*;
import com.core.sqlTool.model.expression.Expression;
import com.core.sqlTool.model.resolver.Resolver;
import com.core.sqlTool.model.visitor.ContextSensitiveExpressionQualifier;
import com.core.sqlTool.model.visitor.ExpressionCalculator;
import com.core.sqlTool.model.visitor.ExpressionValidator;
import com.core.sqlTool.model.visitor.ValueInjector;
import com.core.sqlTool.utils.ModelUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;


public record SelectCommand(List<Expression> expressions) implements ComplexCalculatedCommand {

    @Override
    public LazyTable run(LazyTable lazyTable, Projection projection, Resolver resolver, CalculatorEntry calculatorEntry) {

        var expressionValidator = new ExpressionValidator(lazyTable.columns(), lazyTable.externalRow());
        var columns = getColumns(expressions, expressionValidator, lazyTable);

        var calculatedValueByExpressionMap = expressions.stream()
                .map(expression -> {

                    var isContextSensitiveExpression = expression.accept(new ContextSensitiveExpressionQualifier(lazyTable.columns()));

                    if (isContextSensitiveExpression) {
                        return null;
                    }

                    var value = expression
                            .accept(new ValueInjector(HeaderRow.empty(), lazyTable.externalRow()))
                            .accept(new ExpressionCalculator(resolver, HeaderRow.empty(), lazyTable.externalRow()));

                    return Pair.of(expression, value);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));

        Function<Row, Row> mapper = row -> {

            var headerRow = new HeaderRow(lazyTable.columns(), row);
            var externalRow = lazyTable.externalRow();

            return expressions.stream()
                    .map(expression -> {
                        var value = calculatedValueByExpressionMap.get(expression);
                        if (value != null) {
                            return value;
                        }
                        return expression
                                .accept(new ValueInjector(headerRow, externalRow))
                                .accept(new ExpressionCalculator(resolver, headerRow, externalRow));
                    })
                    .collect(Collectors.collectingAndThen(Collectors.toList(), Row::new));
        };

        return new LazyTable(lazyTable.name(), columns, lazyTable.dataStream().map(mapper), lazyTable.externalRow());
    }

    private List<Column> getColumns(List<Expression> expressions, ExpressionValidator expressionValidator, LazyTable lazyTable) {

        var index = new AtomicInteger(0);

        return expressions.stream()
                .map((expression) -> {

                    if (expression instanceof Column column) {
                        return ModelUtils.resolveColumn(lazyTable.columns(), column);
                    }

                    var columnName = "column_%s".formatted(index.getAndIncrement());
                    var columnType = expression.accept(expressionValidator);

                    return new Column(lazyTable.name(), columnName, columnType);
                })
                .toList();
    }

}
