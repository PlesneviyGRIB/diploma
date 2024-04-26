package com.savchenko.sqlTool.model.command.join;

import com.savchenko.sqlTool.exception.UnexpectedException;
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
import com.savchenko.sqlTool.support.IndexedData;
import com.savchenko.sqlTool.support.JoinStreams;
import com.savchenko.sqlTool.support.WrappedStream;
import com.savchenko.sqlTool.utils.ExpressionUtils;
import com.savchenko.sqlTool.utils.ModelUtils;
import com.savchenko.sqlTool.utils.ValidationUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;

public enum JoinStrategy {

    HASH, MERGE, LOOP;

    public JoinStreams run(LazyTable lazyTable, LazyTable joinedLazyTable, Expression expression, Resolver resolver) {

        var columns = ListUtils.union(lazyTable.columns(), joinedLazyTable.columns());
        ValidationUtils.expectBooleanValueAsResolvedType(expression, columns, joinedLazyTable.externalRow());

        return switch (this) {
            case HASH -> hashImpl(lazyTable, joinedLazyTable, expression, resolver);
            case MERGE -> mergeImpl(lazyTable, joinedLazyTable, expression, resolver);
            case LOOP -> loopImpl(lazyTable, joinedLazyTable, expression, resolver);
        };
    }

    private JoinStreams hashImpl(LazyTable lazyTable, LazyTable joinedLazyTable, Expression expression, Resolver resolver) {
        if (expression instanceof BinaryOperation op && op.operator().equals(Operator.EQ)) {
            return loopImpl(lazyTable, joinedLazyTable, expression, resolver);
        }
        throw new UnexpectedException("Hash/Merge join expects EQUALITY operation, but found '%s'", expression.stringify());
//            var leftExpression = op.left();
//            var rightExpression = op.right();
//
//            var forwardOrder = ExpressionUtils.relatedToTable(lazyTable.columns(), leftExpression, lazyTable.externalRow());
//
//            Supplier<Expression> tableExpression = () -> forwardOrder ? leftExpression : rightExpression;
//            Supplier<Expression> joinedTableExpression = () -> forwardOrder ? rightExpression : leftExpression;
//
//            var isContextSensitiveExpression = expression.accept(new ContextSensitiveExpressionQualifier(lazyTable.columns()));
//
//            Optional<Value<?>> tableValueProvider = isContextSensitiveExpression ?
//                    Optional.empty() : Optional.of(tableExpression.get().accept(new ExpressionCalculator(resolver, ExternalRow.empty())));
//
//            Optional<Value<?>> joinedTableValueProvider = isContextSensitiveExpression ?
//                    Optional.empty() : Optional.of(joinedTableExpression.get().accept(new ExpressionCalculator(resolver, ExternalRow.empty())));
//
//            Function<List<Value<?>>, Value<?>> tableKeyMapper = values -> tableValueProvider.orElseGet(() -> {
//                var externalRow = lazyTable.externalRow().merge(new ExternalRow(lazyTable.columns(), values));
//                return tableExpression.get()
//                        .accept(new ValueInjector(new Row(lazyTable.columns(), values), lazyTable.externalRow()))
//                        .accept(new ExpressionCalculator(resolver, externalRow));
//            });
//
//            Function<List<Value<?>>, Value<?>> joinedTableKeyMapper = values -> joinedTableValueProvider.orElseGet(() -> {
//                var externalRow = lazyTable.externalRow().merge(new ExternalRow(joinedLazyTable.columns(), values));
//                return joinedTableExpression.get()
//                        .accept(new ValueInjector(new Row(joinedLazyTable.columns(), values), joinedLazyTable.externalRow()))
//                        .accept(new ExpressionCalculator(resolver, externalRow));
//            });
//
//            var hashTable = ModelUtils.getIndexedData(lazyTable.dataStream())
//                    .filter(pair -> !ExpressionUtils.columnsContainsNulls(new Row(lazyTable.columns(), pair.getRight()), lazyTable.externalRow(), tableExpression.get()))
//                    .collect(Collectors.toMap(pair -> tableKeyMapper.apply(pair.getRight()), Function.identity()));
//
//            var leftJoinedRowIndexes = new HashSet<Integer>();
//            var rightJoinedRowIndexes = new HashSet<Integer>();
//
//            var dataStream = ModelUtils.getIndexedData(joinedLazyTable.dataStream())
//                    .filter(pair -> !ExpressionUtils.columnsContainsNulls(new Row(joinedLazyTable.columns(), pair.getRight()), joinedLazyTable.externalRow(), joinedTableExpression.get()))
//                    .map(pair2 -> {
//                        var key = joinedTableKeyMapper.apply(pair2.getRight());
//                        var pair1 = hashTable.get(key);
//
//                        if (Objects.nonNull(pair1)) {
//                            leftJoinedRowIndexes.add(pair1.getLeft());
//                            rightJoinedRowIndexes.add(pair2.getLeft());
//                            return ListUtils.union(pair1.getRight(), pair2.getRight());
//                        }
//                        return null;
//                    }).filter(Objects::nonNull);
//
//            return null;
//        }
    }

    private JoinStreams mergeImpl(LazyTable lazyTable, LazyTable joinedLazyTable, Expression expression, Resolver resolver) {
        return hashImpl(lazyTable, joinedLazyTable, expression, resolver);
    }

    private JoinStreams loopImpl(LazyTable lazyTable, LazyTable joinedLazyTable, Expression expression, Resolver resolver) {

        var columns = ListUtils.union(lazyTable.columns(), joinedLazyTable.columns());

        var isContextSensitiveExpression = expression
                .accept(new ContextSensitiveExpressionQualifier(lazyTable.columns()));

        Optional<Value<?>> valueProvider = isContextSensitiveExpression ?
                Optional.empty() : Optional.of(expression.accept(new ExpressionCalculator(resolver, ExternalRow.empty())));

        var usedLeft = new HashSet<Integer>();
        var usedRight = new HashSet<Integer>();

        var notUsedLeft = new LinkedBlockingQueue<IndexedData<List<Value<?>>>>();
        var notUsedRight = new LinkedBlockingQueue<IndexedData<List<Value<?>>>>();

        var notUsedLeftSet = new HashSet<>();
        var notUsedRightSet = new HashSet<>();

        var indexedTableDataStream = IndexedData.indexed(lazyTable.dataStream());
        var indexedJoinedTableDataStream = IndexedData.indexed(joinedLazyTable.dataStream());

        var joinedTableWrappedDataStream = new WrappedStream<>(indexedJoinedTableDataStream);

        var inner = indexedTableDataStream
                .flatMap(row1 -> joinedTableWrappedDataStream.getStream().map(row2 -> Pair.of(row1, row2)))
                .filter(pair -> {
                    var leftIndexedRow = pair.getLeft();
                    var rightIndexedRow = pair.getLeft();
                    var row = ListUtils.union(leftIndexedRow.data(), rightIndexedRow.data());
                    var externalRow = lazyTable.externalRow().merge(new ExternalRow(columns, row));
                    var tableRow = new Row(columns, row);

                    var joined = ExpressionUtils.columnsContainsNulls(tableRow, externalRow, expression) &&
                            ((BooleanValue) valueProvider.orElseGet(() -> expression
                                    .accept(new ValueInjector(tableRow, lazyTable.externalRow()))
                                    .accept(new ExpressionCalculator(resolver, externalRow))
                            )).value();

                    if (joined) {
                        usedLeft.add(leftIndexedRow.index());
                        usedRight.add(rightIndexedRow.index());
                    } else {
                        if (!notUsedLeftSet.contains(leftIndexedRow.index())) {
                            notUsedLeftSet.add(leftIndexedRow.index());
                            notUsedLeft.add(leftIndexedRow);
                        }
                        if (!notUsedRightSet.contains(rightIndexedRow.index())) {
                            notUsedRightSet.add(rightIndexedRow.index());
                            notUsedRight.add(rightIndexedRow);
                        }
                    }
                    return joined;
                })
                .map(pair -> ListUtils.union(pair.getLeft().data(), pair.getRight().data()));

        var leftRemainder = notUsedLeft.stream()
                .filter(indexedRow -> !usedLeft.contains(indexedRow.index()))
                .map(indexedRow -> ListUtils.union(indexedRow.data(), ModelUtils.emptyRow(joinedLazyTable)));

        var rightRemainder = notUsedRight.stream()
                .filter(indexedRow -> !usedRight.contains(indexedRow.index()))
                .map(indexedRow -> ListUtils.union(ModelUtils.emptyRow(lazyTable), indexedRow.data()));

        return new JoinStreams(inner, leftRemainder, rightRemainder);
    }

}
