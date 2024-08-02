package com.core.sqlTool.model.command.join;

import com.core.sqlTool.model.complexity.CalculatorEntry;
import com.core.sqlTool.model.domain.HeaderRow;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.domain.Row;
import com.core.sqlTool.model.expression.BooleanValue;
import com.core.sqlTool.model.expression.Expression;
import com.core.sqlTool.model.expression.Value;
import com.core.sqlTool.model.resolver.Resolver;
import com.core.sqlTool.model.visitor.ContextSensitiveExpressionQualifier;
import com.core.sqlTool.model.visitor.ExpressionCalculator;
import com.core.sqlTool.support.IndexedData;
import com.core.sqlTool.support.JoinStreams;
import com.core.sqlTool.support.WrappedStream;
import com.core.sqlTool.utils.ExpressionUtils;
import com.core.sqlTool.utils.ModelUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;

public record LoopJoinStrategy() implements JoinStrategy {

    @Override
    public JoinStreams run(LazyTable lazyTable, LazyTable joinedLazyTable, Expression expression, Resolver resolver, CalculatorEntry calculatorEntry) {

        var columns = ListUtils.union(lazyTable.columns(), joinedLazyTable.columns());

        var isContextSensitiveExpression = expression
                .accept(new ContextSensitiveExpressionQualifier(lazyTable.columns()));

        Optional<Value<?>> valueProvider = isContextSensitiveExpression ?
                Optional.empty() :
                Optional.of(expression.accept(new ExpressionCalculator(resolver, HeaderRow.empty(), lazyTable.externalRow())).getValue());

        var usedLeft = new HashSet<Integer>();
        var usedRight = new HashSet<Integer>();

        var notUsedLeft = new LinkedBlockingQueue<IndexedData<Row>>();
        var notUsedRight = new LinkedBlockingQueue<IndexedData<Row>>();

        var notUsedLeftSet = new HashSet<Integer>();
        var notUsedRightSet = new HashSet<Integer>();

        var indexedTableDataStream = IndexedData.indexed(lazyTable.dataStream());
        var indexedJoinedTableDataStream = IndexedData.indexed(joinedLazyTable.dataStream());

        var joinedTableWrappedDataStream = new WrappedStream<>(indexedJoinedTableDataStream);

        var inner = indexedTableDataStream
                .flatMap(row1 -> joinedTableWrappedDataStream.getStream().map(row2 -> Pair.of(row1, row2)))
                .peek(calculatorEntry::count)
                .filter(pair -> {
                    var leftIndexedRow = pair.getLeft();
                    var rightIndexedRow = pair.getRight();
                    var row = Row.merge(leftIndexedRow.data(), rightIndexedRow.data());
                    var headerRow = new HeaderRow(columns, row);
                    var externalRow = lazyTable.externalRow();

                    var joined = !ExpressionUtils.columnsContainsNulls(headerRow, externalRow, expression) &&
                            ((BooleanValue) valueProvider.orElseGet(() -> expression.accept(new ExpressionCalculator(resolver, headerRow, externalRow)).getValue())).value();

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
                .map(pair -> Row.merge(pair.getLeft().data(), pair.getRight().data()));

        var leftRemainder = notUsedLeft.stream()
                .filter(indexedRow -> !usedLeft.contains(indexedRow.index()))
                .map(IndexedData::data)
                .map(row -> Row.merge(row, ModelUtils.emptyRow(joinedLazyTable)));

        var rightRemainder = notUsedRight.stream()
                .filter(indexedRow -> !usedRight.contains(indexedRow.index()))
                .map(IndexedData::data)
                .map(row -> Row.merge(ModelUtils.emptyRow(lazyTable), row));

        return new JoinStreams(inner, leftRemainder, rightRemainder);
    }

}
