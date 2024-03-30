package com.savchenko.sqlTool.model.command.join;

import com.savchenko.sqlTool.exception.UnexpectedException;
import com.savchenko.sqlTool.exception.UnexpectedExpressionException;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.model.expression.BinaryOperation;
import com.savchenko.sqlTool.model.expression.BooleanValue;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.expression.Value;
import com.savchenko.sqlTool.model.operator.Operator;
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

    public Triple<List<List<Value<?>>>, Set<Integer>, Set<Integer>> run(Table table, Table joinedTable, Expression expression) {
        return switch (this) {
            case HASH -> hashImpl(table, joinedTable, expression);
            case MERGE -> mergeImpl(table, joinedTable, expression);
            case LOOP -> loopImpl(table, joinedTable, expression);
        };
    }

    private Triple<List<List<Value<?>>>, Set<Integer>, Set<Integer>> hashImpl(Table table, Table joinedTable, Expression expression) {
        if (expression instanceof BinaryOperation op && op.operator().equals(Operator.EQ)) {
            var leftExpression = op.left();
            var rightExpression = op.right();

            var forwardOrder = ExpressionUtils.relatedToTable(table.columns(), leftExpression);

            Supplier<Expression> tableExpression = () -> forwardOrder ? leftExpression : rightExpression;
            Supplier<Expression> joinedTableExpression = () -> forwardOrder ? rightExpression : leftExpression;

            Function<List<Value<?>>, Value<?>> tableKeyMapper = values -> {
                var columnValue = ModelUtils.columnValueMap(table.columns(), values);
                return tableExpression.get()
                        .accept(new ValueInjector(columnValue, Map.of()))
                        .accept(new ExpressionCalculator());
            };

            Function<List<Value<?>>, Value<?>> joinedTableKeyMapper = values -> {
                var columnValue = ModelUtils.columnValueMap(joinedTable.columns(), values);
                return joinedTableExpression.get()
                        .accept(new ValueInjector(columnValue, Map.of()))
                        .accept(new ExpressionCalculator());
            };

            var hashTable = ModelUtils.getIndexedData(table.data()).stream()
                    .filter(pair -> {
                        var columnValue = ModelUtils.columnValueMap(table.columns(), pair.getRight());
                        return !ExpressionUtils.columnsContainsNulls(columnValue, tableExpression.get());
                    })
                    .collect(Collectors.toMap(pair -> tableKeyMapper.apply(pair.getRight()), Function.identity()));

            var leftJoinedRowIndexes = new HashSet<Integer>();
            var rightJoinedRowIndexes = new HashSet<Integer>();

            var data = ModelUtils.getIndexedData(joinedTable.data()).stream()
                    .filter(pair -> {
                        var columnValue = ModelUtils.columnValueMap(joinedTable.columns(), pair.getRight());
                        return !ExpressionUtils.columnsContainsNulls(columnValue, joinedTableExpression.get());
                    })
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

    private Triple<List<List<Value<?>>>, Set<Integer>, Set<Integer>> mergeImpl(Table table, Table joinedTable, Expression expression) {
        return null;
    }

    private Triple<List<List<Value<?>>>, Set<Integer>, Set<Integer>> loopImpl(Table table, Table joinedTable, Expression expression) {
        var columns = ListUtils.union(table.columns(), joinedTable.columns());

        var leftJoinedRowIndexes = new HashSet<Integer>();
        var leftIndexedData = ModelUtils.getIndexedData(table.data());

        var rightJoinedRowIndexes = new HashSet<Integer>();
        var rightIndexedData = ModelUtils.getIndexedData(joinedTable.data());

        var data = leftIndexedData.stream()
                .flatMap(pair1 -> {
                    var row1 = pair1.getRight();
                    return rightIndexedData.stream()
                            .map(pair2 -> {
                                var row2 = pair2.getRight();
                                var row = ListUtils.union(row1, row2);

                                var columnValueMap = ModelUtils.columnValueMap(columns, row);

                                if (ExpressionUtils.columnsContainsNulls(columnValueMap, expression)) {
                                    return null;
                                }

                                var value = expression
                                        .accept(new ValueInjector(columnValueMap, Map.of()))
                                        .accept(new ExpressionCalculator());

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
