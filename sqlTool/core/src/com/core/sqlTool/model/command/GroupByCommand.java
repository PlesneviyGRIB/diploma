package com.core.sqlTool.model.command;

import com.core.sqlTool.model.command.aggregation.AggregationFunction;
import com.core.sqlTool.model.complexity.CalculatorEntry;
import com.core.sqlTool.model.domain.*;
import com.core.sqlTool.model.expression.Expression;
import com.core.sqlTool.model.expression.Value;
import com.core.sqlTool.model.expression.ValueList;
import com.core.sqlTool.model.resolver.Resolver;
import com.core.sqlTool.model.visitor.ContextSensitiveExpressionQualifier;
import com.core.sqlTool.model.visitor.ExpressionCalculator;
import com.core.sqlTool.model.visitor.ExpressionValidator;
import com.core.sqlTool.model.visitor.ValueInjector;
import com.core.sqlTool.utils.ModelUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record GroupByCommand(List<Expression> expressions,
                             List<Pair<Expression, AggregationFunction>> aggregations) implements ComplexCalculatedCommand {

    @Override
    public LazyTable run(LazyTable lazyTable, Projection projection, Resolver resolver, CalculatorEntry calculatorEntry) {

        var expressionValidator = new ExpressionValidator(lazyTable.columns(), lazyTable.externalRow());
        var aggregationExpressions = aggregations.stream().map(Pair::getLeft).toList();
        var columns = getColumns(ListUtils.union(expressions, aggregationExpressions), expressionValidator, lazyTable);

        Map<Expression, Value<?>> calculatedValueByExpressionMap = Stream.of(expressions.stream(), aggregations.stream().map(Pair::getLeft))
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

                    var values = expressions.stream()
                            .map(expression -> {
                                var value = calculatedValueByExpressionMap.get(expression);
                                if (value != null) {
                                    return value;
                                }
                                return expression
                                        .accept(new ValueInjector(headerRow, lazyTable.externalRow()))
                                        .accept(new ExpressionCalculator(resolver, headerRow, lazyTable.externalRow()));
                            })
                            .collect(Collectors.toList());

                    groupedRows.putIfAbsent(values, new ArrayList<>());
                    groupedRows.get(values).add(row);
                });

        var data = groupedRows.entrySet().stream()
                .map(entry -> {
                    var groupKeyValues = entry.getKey();
                    var aggregationValues = getAggregatedValues(entry.getValue(), lazyTable, calculatedValueByExpressionMap, resolver);
                    return new Row(ListUtils.union(groupKeyValues, aggregationValues));
                })
                .toList();

        return new LazyTable(lazyTable.name(), columns, data.stream(), lazyTable.externalRow());
    }

    private List<Column> getColumns(List<Expression> expressions, ExpressionValidator expressionValidator, LazyTable lazyTable) {

        var index = new AtomicInteger(0);

        return expressions.stream()
                .map((expression) -> ModelUtils.getColumnFromExpression(expression, lazyTable, index.getAndIncrement(), expressionValidator))
                .toList();
    }

    private List<Value<?>> getAggregatedValues(List<Row> groupOfRows, LazyTable lazyTable, Map<Expression, Value<?>> calculatedValueByExpressionMap, Resolver resolver) {
        return aggregations.stream()
                .map(aggregation -> {

                    var expression = aggregation.getLeft();
                    var aggregationFunction = aggregation.getRight();

                    var values = groupOfRows.stream()
                            .map(row -> {

                                var value = calculatedValueByExpressionMap.get(expression);
                                if (value != null) {
                                    return value;
                                }
                                var headerRow = new HeaderRow(lazyTable.columns(), row);
                                return expression
                                        .accept(new ValueInjector(headerRow, lazyTable.externalRow()))
                                        .accept(new ExpressionCalculator(resolver, headerRow, lazyTable.externalRow()));
                            })
                            .toList();

                    return aggregationFunction.aggregate(new ValueList(values, (Class<? extends Value<?>>) values.get(0).getClass()));
                })
                .collect(Collectors.toList());
    }

}
