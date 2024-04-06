package com.savchenko.sqlTool.model.command.join;

import com.savchenko.sqlTool.exception.UnexpectedException;
import com.savchenko.sqlTool.exception.UnexpectedExpressionException;
import com.savchenko.sqlTool.model.domain.ExternalRow;
import com.savchenko.sqlTool.model.domain.Row;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.model.expression.BinaryOperation;
import com.savchenko.sqlTool.model.expression.BooleanValue;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.expression.Value;
import com.savchenko.sqlTool.model.operator.Operator;
import com.savchenko.sqlTool.model.resolver.Resolver;
import com.savchenko.sqlTool.model.visitor.ContextSensitiveExpressionQualifier;
import com.savchenko.sqlTool.model.visitor.ExpressionCalculator;
import com.savchenko.sqlTool.model.visitor.ValueInjector;
import com.savchenko.sqlTool.utils.ExpressionUtils;
import com.savchenko.sqlTool.utils.ModelUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public enum JoinStrategy {
    HASH, MERGE, LOOP;

    public Integer getStrategyComplicity(Table table, Table joinedTable) {
        switch (this) {
            case HASH:
                return table.data().size() + joinedTable.data().size();
            case MERGE:
                return null;
            case LOOP:
                return table.data().size() * joinedTable.data().size();
            default:
                return null;
        }
    }

    public Triple<List<List<Value<?>>>, Set<Integer>, Set<Integer>> run(Table table, Table joinedTable, Expression expression, Resolver resolver) {
        return switch (this) {
            case HASH -> hashImpl(table, joinedTable, expression, resolver);
            case MERGE -> mergeImpl(table, joinedTable, expression, resolver);
            case LOOP -> loopImpl(table, joinedTable, expression, resolver);
        };
    }

    private Triple<List<List<Value<?>>>, Set<Integer>, Set<Integer>> hashImpl(Table table, Table joinedTable, Expression expression, Resolver resolver) {
        if (expression instanceof BinaryOperation op && op.operator().equals(Operator.EQ)) {
            var leftExpression = op.left();
            var rightExpression = op.right();

            var forwardOrder = ExpressionUtils.relatedToTable(table.columns(), leftExpression, table.externalRow());

            Supplier<Expression> tableExpression = () -> forwardOrder ? leftExpression : rightExpression;
            Supplier<Expression> joinedTableExpression = () -> forwardOrder ? rightExpression : leftExpression;

            var isContextSensitiveExpression = expression.accept(new ContextSensitiveExpressionQualifier(resolver, table));

            Optional<Value<?>> tableValueProvider = isContextSensitiveExpression ?
                    Optional.empty() : Optional.of(tableExpression.get().accept(new ExpressionCalculator(resolver, ExternalRow.empty())));

            Optional<Value<?>> joinedTableValueProvider = isContextSensitiveExpression ?
                    Optional.empty() : Optional.of(joinedTableExpression.get().accept(new ExpressionCalculator(resolver, ExternalRow.empty())));

            Function<List<Value<?>>, Value<?>> tableKeyMapper = values -> tableValueProvider.orElseGet(() -> {
                var externalRow = table.externalRow().merge(new ExternalRow(table.columns(), values));
                return tableExpression.get()
                        .accept(new ValueInjector(new Row(table.columns(), values), table.externalRow()))
                        .accept(new ExpressionCalculator(resolver, externalRow));
            });

            Function<List<Value<?>>, Value<?>> joinedTableKeyMapper = values -> joinedTableValueProvider.orElseGet(() -> {
                var externalRow = table.externalRow().merge(new ExternalRow(joinedTable.columns(), values));
                return joinedTableExpression.get()
                        .accept(new ValueInjector(new Row(joinedTable.columns(), values), joinedTable.externalRow()))
                        .accept(new ExpressionCalculator(resolver, externalRow));
            });

            var hashTable = ModelUtils.getIndexedData(table.data()).stream()
                    .filter(pair -> !ExpressionUtils.columnsContainsNulls(new Row(table.columns(), pair.getRight()), table.externalRow(), tableExpression.get()))
                    .collect(Collectors.toMap(pair -> tableKeyMapper.apply(pair.getRight()), Function.identity()));

            var leftJoinedRowIndexes = new HashSet<Integer>();
            var rightJoinedRowIndexes = new HashSet<Integer>();

            var data = ModelUtils.getIndexedData(joinedTable.data()).stream()
                    .filter(pair -> !ExpressionUtils.columnsContainsNulls(new Row(joinedTable.columns(), pair.getRight()), joinedTable.externalRow(), joinedTableExpression.get()))
                    .map(pair2 -> {
                        var key = joinedTableKeyMapper.apply(pair2.getRight());
                        var pair1 = hashTable.get(key);

                        if (Objects.nonNull(pair1)) {
                            leftJoinedRowIndexes.add(pair1.getLeft());
                            rightJoinedRowIndexes.add(pair2.getLeft());
                            return ListUtils.union(pair1.getRight(), pair2.getRight());
                        }
                        return null;
                    }).filter(Objects::nonNull).toList();

            return Triple.of(data, leftJoinedRowIndexes, rightJoinedRowIndexes);
        }
        throw new UnexpectedException("Hash join expects EQUALITY operation, but found '%s'", expression.stringify());
    }

    private Triple<List<List<Value<?>>>, Set<Integer>, Set<Integer>> mergeImpl(Table table, Table joinedTable, Expression expression, Resolver resolver) {
        return null;
    }

    private Triple<List<List<Value<?>>>, Set<Integer>, Set<Integer>> loopImpl(Table table, Table joinedTable, Expression expression, Resolver resolver) {
        var columns = ListUtils.union(table.columns(), joinedTable.columns());

        var leftJoinedRowIndexes = new HashSet<Integer>();
        var leftIndexedData = ModelUtils.getIndexedData(table.data());

        var rightJoinedRowIndexes = new HashSet<Integer>();
        var rightIndexedData = ModelUtils.getIndexedData(joinedTable.data());

        var isContextSensitiveExpression = expression.accept(new ContextSensitiveExpressionQualifier(resolver, table));

        Optional<Value<?>> valueProvider = isContextSensitiveExpression ?
                Optional.empty() : Optional.of(expression.accept(new ExpressionCalculator(resolver, ExternalRow.empty())));

        var data = leftIndexedData.stream()
                .flatMap(pair1 -> {
                    var row1 = pair1.getRight();
                    return rightIndexedData.stream()
                            .map(pair2 -> {
                                var row2 = pair2.getRight();
                                var row = ListUtils.union(row1, row2);
                                var externalRow = table.externalRow().merge(new ExternalRow(columns, row));
                                var tableRow = new Row(columns, row);

                                if (ExpressionUtils.columnsContainsNulls(tableRow, externalRow, expression)) {
                                    return null;
                                }

                                var value = valueProvider.orElseGet(() -> expression
                                        .accept(new ValueInjector(tableRow, table.externalRow()))
                                        .accept(new ExpressionCalculator(resolver, externalRow))
                                );

                                if (value instanceof BooleanValue bv) {
                                    if (bv.value()) {
                                        leftJoinedRowIndexes.add(pair1.getLeft());
                                        rightJoinedRowIndexes.add(pair2.getLeft());
                                        return row;
                                    }
                                    return null;
                                }
                                throw new UnexpectedExpressionException(value);
                            }).filter(Objects::nonNull);
                }).toList();

        return Triple.of(data, leftJoinedRowIndexes, rightJoinedRowIndexes);
    }

}
