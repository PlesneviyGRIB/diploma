package com.core.sqlTool.model.command;

import com.core.sqlTool.model.command.aggregation.AggregationFunction;
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
import com.core.sqlTool.model.visitor.ValueInjector;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record GroupByCommand(List<Expression> expressions,
                             List<Pair<Expression, AggregationFunction>> aggregations) implements ComplexCalculatedCommand {

    @Override
    public LazyTable run(LazyTable lazyTable, Projection projection, Resolver resolver, CalculatorEntry calculatorEntry) {

        var calculatedValueByExpressionMap = Stream.of(expressions.stream(), aggregations.stream().map(Pair::getLeft))
                .flatMap(s -> s)
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

        var groupedRows = new HashMap<List<Value<?>>, List<Row>>();
        lazyTable.dataStream()
                .forEach(row -> {

                    var headerRow = new HeaderRow(lazyTable.columns(), row);
                    var externalRow = lazyTable.externalRow();

                    var values = expressions.stream()
                            .map(expression -> {
                                var value = calculatedValueByExpressionMap.get(expression);
                                if (value != null) {
                                    return value;
                                }
                                return expression
                                        .accept(new ValueInjector(headerRow, externalRow))
                                        .accept(new ExpressionCalculator(resolver, headerRow, externalRow));
                            })
                            .toList();

                    groupedRows.putIfAbsent(values, new ArrayList<>()).add(row);
                });


        return new LazyTable(table.name(), table.columns(), data.stream(), lazyTable.externalRow());
    }

}
