package com.core.sqlTool.model.command;


import com.core.sqlTool.model.complexity.CalculatorEntry;
import com.core.sqlTool.model.domain.HeaderRow;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.domain.Projection;
import com.core.sqlTool.model.domain.Row;
import com.core.sqlTool.model.expression.Expression;
import com.core.sqlTool.model.expression.Value;
import com.core.sqlTool.model.resolver.Resolver;
import com.core.sqlTool.model.visitor.ContextSensitiveExpressionQualifier;
import com.core.sqlTool.model.visitor.ExpressionCalculator;
import com.core.sqlTool.model.visitor.ExpressionValidator;
import com.core.sqlTool.model.visitor.ValueInjector;
import com.core.sqlTool.utils.ModelUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public record OrderByCommand(List<Pair<Expression, Boolean>> orders) implements MultipleExpressionsCommand {

    @Override
    public LazyTable run(LazyTable lazyTable, Projection projection, Resolver resolver, CalculatorEntry calculatorEntry) {

        var calculatedValueByExpressionMap = orders.stream().map(Pair::getLeft)
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
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight, (v1, v2) -> v2));

        Comparator<Row> rowsComparator = (row1, row2) -> orders.stream()
                .map(order -> {

                    var expression = order.getLeft();

                    Function<Row, Value<?>> calculateValue = row -> {
                        var value = calculatedValueByExpressionMap.get(expression);
                        if (value != null) {
                            return value;
                        }
                        var headerRow = new HeaderRow(lazyTable.columns(), row);
                        return expression
                                .accept(new ValueInjector(headerRow, lazyTable.externalRow()))
                                .accept(new ExpressionCalculator(resolver, headerRow, lazyTable.externalRow()));
                    };

                    var expressionValidator = new ExpressionValidator(lazyTable.columns(), lazyTable.externalRow());
                    var valueType = expression.accept(expressionValidator);

                    var value1 = calculateValue.apply(row1);
                    var value2 = calculateValue.apply(row2);

                    var res = ModelUtils.compareValues(value1, value2, valueType);
                    if (!order.getRight()) {
                        res *= -1;
                    }
                    return res;
                })
                .filter(res -> res != 0)
                .findFirst().orElse(0);

        return new LazyTable(lazyTable.name(), lazyTable.columns(), lazyTable.dataStream().sorted(rowsComparator), lazyTable.externalRow());
    }

    @Override
    public List<Expression> getExpressions() {
        return orders.stream().map(Pair::getLeft).toList();
    }

}
