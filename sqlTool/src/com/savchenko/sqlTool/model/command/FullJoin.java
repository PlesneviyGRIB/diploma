package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.exception.UnsupportedTypeException;
import com.savchenko.sqlTool.model.expression.BooleanValue;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.visitor.ExpressionCalculator;
import com.savchenko.sqlTool.model.visitor.ValueInjector;
import com.savchenko.sqlTool.model.structure.Table;
import com.savchenko.sqlTool.repository.Projection;
import com.savchenko.sqlTool.utils.ModelUtils;
import org.apache.commons.collections4.ListUtils;

import java.util.*;
import java.util.stream.Stream;

public class FullJoin extends Join {
    public FullJoin(List<Command> commands, Expression expression, JoinStrategy strategy, Projection projection) {
        super(commands, expression, strategy, projection);
    }

    @Override
    public Table run(Table table, Table joinedTable) {
        var columns = ListUtils.union(table.columns(), joinedTable.columns());

        var leftJoinedRowIndexes = new HashSet<Integer>();
        var leftIndexedData = ModelUtils.getIndexedData(table.data());

        var rightJoinedRowIndexes = new HashSet<Integer>();
        var rightIndexedData = ModelUtils.getIndexedData(joinedTable.data());

        var joinedData = leftIndexedData.stream()
                .flatMap(pair1 -> rightIndexedData.stream().map(pair2 -> {
                    var row = ListUtils.union(pair1.getRight(), pair2.getRight());

                    if(expressionContainNull(columns, row)) {
                        return null;
                    }
                    var value = expression
                            .accept(new ValueInjector(columns, row, Map.of()))
                            .accept(new ExpressionCalculator());

                    if(value instanceof BooleanValue bv) {
                        if(bv.value()) {
                            leftJoinedRowIndexes.add(pair1.getLeft());
                            rightJoinedRowIndexes.add(pair2.getLeft());
                            return row;
                        }
                        return null;
                    }
                    throw new UnsupportedTypeException();
                }).filter(Objects::nonNull).toList().stream()).toList();

        var leftRemainder = leftIndexedData.stream()
                .filter(pair -> !leftJoinedRowIndexes.contains(pair.getLeft()))
                .map(pair -> ListUtils.union(pair.getRight(), ModelUtils.emptyRow(joinedTable)))
                .toList();

        var rightRemainder = rightIndexedData.stream()
                .filter(pair -> !rightJoinedRowIndexes.contains(pair.getLeft()))
                .map(pair -> ListUtils.union(ModelUtils.emptyRow(table), pair.getRight()))
                .toList();

        var data = Stream.of(joinedData, leftRemainder, rightRemainder).flatMap(Collection::stream).toList();

        return new Table(null, columns, data, List.of());
    }
}
