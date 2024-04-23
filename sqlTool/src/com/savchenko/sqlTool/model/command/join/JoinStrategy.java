package com.savchenko.sqlTool.model.command.join;

import com.savchenko.sqlTool.exception.UnexpectedException;
import com.savchenko.sqlTool.exception.UnexpectedExpressionException;
import com.savchenko.sqlTool.model.domain.ExternalRow;
import com.savchenko.sqlTool.model.domain.LazyTable;
import com.savchenko.sqlTool.model.domain.Row;
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
import java.util.stream.Stream;

public enum JoinStrategy {
    HASH, MERGE, LOOP;

    public Integer getStrategyComplexity(LazyTable lazyTable, LazyTable joinedLazyTable) {
        switch (this) {
            case HASH:
                //return table.data().size() + joinedTable.data().size();
                return 0;
            case MERGE:
                //return table.data().size() + joinedTable.data().size();
                return 0;
            case LOOP:
                //return table.data().size() * joinedTable.data().size();
                return 0;
            default:
                return null;
        }
    }

    public Triple<Stream<List<Value<?>>>, Set<Integer>, Set<Integer>> run(LazyTable lazyTable, LazyTable joinedLazyTable, Expression expression, Resolver resolver) {
        return switch (this) {
            case HASH -> hashImpl(lazyTable, joinedLazyTable, expression, resolver);
            case MERGE -> mergeImpl(lazyTable, joinedLazyTable, expression, resolver);
            case LOOP -> loopImpl(lazyTable, joinedLazyTable, expression, resolver);
        };
    }

    private Triple<Stream<List<Value<?>>>, Set<Integer>, Set<Integer>> hashImpl(LazyTable lazyTable, LazyTable joinedLazyTable, Expression expression, Resolver resolver) {
        if (expression instanceof BinaryOperation op && op.operator().equals(Operator.EQ)) {
            var leftExpression = op.left();
            var rightExpression = op.right();

            var forwardOrder = ExpressionUtils.relatedToTable(lazyTable.columns(), leftExpression, lazyTable.externalRow());

            Supplier<Expression> tableExpression = () -> forwardOrder ? leftExpression : rightExpression;
            Supplier<Expression> joinedTableExpression = () -> forwardOrder ? rightExpression : leftExpression;

            var isContextSensitiveExpression = expression.accept(new ContextSensitiveExpressionQualifier(lazyTable.columns()));

            Optional<Value<?>> tableValueProvider = isContextSensitiveExpression ?
                    Optional.empty() : Optional.of(tableExpression.get().accept(new ExpressionCalculator(resolver, ExternalRow.empty())));

            Optional<Value<?>> joinedTableValueProvider = isContextSensitiveExpression ?
                    Optional.empty() : Optional.of(joinedTableExpression.get().accept(new ExpressionCalculator(resolver, ExternalRow.empty())));

            Function<List<Value<?>>, Value<?>> tableKeyMapper = values -> tableValueProvider.orElseGet(() -> {
                var externalRow = lazyTable.externalRow().merge(new ExternalRow(lazyTable.columns(), values));
                return tableExpression.get()
                        .accept(new ValueInjector(new Row(lazyTable.columns(), values), lazyTable.externalRow()))
                        .accept(new ExpressionCalculator(resolver, externalRow));
            });

            Function<List<Value<?>>, Value<?>> joinedTableKeyMapper = values -> joinedTableValueProvider.orElseGet(() -> {
                var externalRow = lazyTable.externalRow().merge(new ExternalRow(joinedLazyTable.columns(), values));
                return joinedTableExpression.get()
                        .accept(new ValueInjector(new Row(joinedLazyTable.columns(), values), joinedLazyTable.externalRow()))
                        .accept(new ExpressionCalculator(resolver, externalRow));
            });

            var hashTable = ModelUtils.getIndexedData(lazyTable.dataStream())
                    .filter(pair -> !ExpressionUtils.columnsContainsNulls(new Row(lazyTable.columns(), pair.getRight()), lazyTable.externalRow(), tableExpression.get()))
                    .collect(Collectors.toMap(pair -> tableKeyMapper.apply(pair.getRight()), Function.identity()));

            var leftJoinedRowIndexes = new HashSet<Integer>();
            var rightJoinedRowIndexes = new HashSet<Integer>();

            var dataStream = ModelUtils.getIndexedData(joinedLazyTable.dataStream())
                    .filter(pair -> !ExpressionUtils.columnsContainsNulls(new Row(joinedLazyTable.columns(), pair.getRight()), joinedLazyTable.externalRow(), joinedTableExpression.get()))
                    .map(pair2 -> {
                        var key = joinedTableKeyMapper.apply(pair2.getRight());
                        var pair1 = hashTable.get(key);

                        if (Objects.nonNull(pair1)) {
                            leftJoinedRowIndexes.add(pair1.getLeft());
                            rightJoinedRowIndexes.add(pair2.getLeft());
                            return ListUtils.union(pair1.getRight(), pair2.getRight());
                        }
                        return null;
                    }).filter(Objects::nonNull);

            return Triple.of(dataStream, leftJoinedRowIndexes, rightJoinedRowIndexes);
        }
        throw new UnexpectedException("Hash/Merge join expects EQUALITY operation, but found '%s'", expression.stringify());
    }

    private Triple<Stream<List<Value<?>>>, Set<Integer>, Set<Integer>> mergeImpl(LazyTable lazyTable, LazyTable joinedLazyTable, Expression expression, Resolver resolver) {
        return hashImpl(lazyTable, joinedLazyTable, expression, resolver);
    }

    private Triple<Stream<List<Value<?>>>, Set<Integer>, Set<Integer>> loopImpl(LazyTable lazyTable, LazyTable joinedLazyTable, Expression expression, Resolver resolver) {
        var columns = ListUtils.union(lazyTable.columns(), joinedLazyTable.columns());

        var leftJoinedRowIndexes = new HashSet<Integer>();
        var leftIndexedData = ModelUtils.getIndexedData(lazyTable.dataStream());

        var rightJoinedRowIndexes = new HashSet<Integer>();
        var rightIndexedData = ModelUtils.getIndexedData(joinedLazyTable.dataStream());

        var isContextSensitiveExpression = expression.accept(new ContextSensitiveExpressionQualifier(lazyTable.columns()));

        Optional<Value<?>> valueProvider = isContextSensitiveExpression ?
                Optional.empty() : Optional.of(expression.accept(new ExpressionCalculator(resolver, ExternalRow.empty())));

        var dataStream = leftIndexedData
                .flatMap(pair1 -> {
                    var row1 = pair1.getRight();
                    return rightIndexedData
                            .map(pair2 -> {
                                var row2 = pair2.getRight();
                                var row = ListUtils.union(row1, row2);
                                var externalRow = lazyTable.externalRow().merge(new ExternalRow(columns, row));
                                var tableRow = new Row(columns, row);

                                if (ExpressionUtils.columnsContainsNulls(tableRow, externalRow, expression)) {
                                    return null;
                                }

                                var value = valueProvider.orElseGet(() -> expression
                                        .accept(new ValueInjector(tableRow, lazyTable.externalRow()))
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
                });

        return Triple.of(dataStream, leftJoinedRowIndexes, rightJoinedRowIndexes);
    }

}
