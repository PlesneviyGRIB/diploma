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
import com.core.sqlTool.model.visitor.ExpressionCalculator;
import com.core.sqlTool.model.visitor.ExpressionResultTypeResolver;
import com.core.sqlTool.model.visitor.ValueInjector;
import com.core.sqlTool.utils.ExpressionUtils;
import com.core.sqlTool.utils.ModelUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record GroupByCommand(List<Expression> expressions,
                             List<Pair<Expression, AggregationFunction>> aggregations) implements MultipleExpressionsCommand {

    @Override
    public LazyTable run(LazyTable lazyTable, Projection projection, Resolver resolver, CalculatorEntry calculatorEntry) {

        var expressionResultTypeResolver = new ExpressionResultTypeResolver(lazyTable.columns(), lazyTable.externalRow());
        var aggregationExpressions = aggregations.stream().map(Pair::getLeft).toList();
        var columns = ListUtils.union(expressions, aggregationExpressions).stream()
                .map((expression) -> ModelUtils.getColumnFromExpression(expression, lazyTable, expressionResultTypeResolver))
                .toList();

        var allExpressions = ListUtils.union(expressions, aggregations.stream().map(Pair::getLeft).toList());
        var calculatedValueByExpressionMap = ExpressionUtils.calculateContextInsensitiveExpressions(allExpressions, lazyTable, resolver);

        var groupedRows = new HashMap<List<Value<?>>, List<Row>>();
        lazyTable.dataStream()
                .forEach(row -> {
                    var headerRow = new HeaderRow(lazyTable.columns(), row);

                    var values = expressions.stream()
                            .map(expression -> ExpressionUtils.calculateExpression(expression, headerRow, lazyTable.externalRow(), resolver, calculatedValueByExpressionMap))
                            .collect(Collectors.<Value<?>>toList());

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

    @Override
    public List<Expression> getExpressions() {
        var aggregationExpressions = aggregations.stream().map(Pair::getLeft).toList();
        return ListUtils.union(expressions, aggregationExpressions);
    }

    private List<Value<?>> getAggregatedValues(List<Row> groupOfRows, LazyTable lazyTable, Map<Expression, Value<?>> calculatedValueByExpressionMap, Resolver resolver) {
        return aggregations.stream()
                .map(aggregation -> {

                    var expression = aggregation.getLeft();
                    var aggregationFunction = aggregation.getRight();

                    List<Value<?>> values = groupOfRows.stream()
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

                    return aggregationFunction.aggregate(ModelUtils.toSingleTypeValues(values));
                })
                .collect(Collectors.toList());
    }

}
