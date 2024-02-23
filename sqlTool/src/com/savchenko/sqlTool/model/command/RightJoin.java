package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.exception.UnsupportedTypeException;
import com.savchenko.sqlTool.model.expression.BooleanValue;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.expression.visitor.ExpressionCalculator;
import com.savchenko.sqlTool.model.expression.visitor.ExpressionValidator;
import com.savchenko.sqlTool.model.expression.visitor.ValueInjector;
import com.savchenko.sqlTool.model.structure.Table;
import com.savchenko.sqlTool.repository.Projection;
import com.savchenko.sqlTool.utils.ModelUtils;
import org.apache.commons.collections4.ListUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class RightJoin extends Join {
    public RightJoin(String table, Expression<?> expression, JoinStrategy strategy, Projection projection) {
        super(table, expression, strategy, projection);
    }

    @Override
    public Table run(Table table) {
        var joinedTable = projection.getByName(this.table);
        var columns = ListUtils.union(table.columns(), joinedTable.columns());

        var joinedRowIndexes = new HashSet<Integer>();
        var indexedData = ModelUtils.getIndexedData(joinedTable.data());

        var joinedData = table.data().stream()
                .flatMap(row1 -> indexedData.stream().map(pair -> {
                    var row = ListUtils.union(row1, pair.getRight());

                    if(expressionContainNull(columns, row)) {
                        return null;
                    }

                    var value = expression
                            .accept(new ValueInjector(columns, row))
                            .accept(new ExpressionCalculator());

                    if(value instanceof BooleanValue bv) {
                        if(bv.value()) {
                            joinedRowIndexes.add(pair.getLeft());
                            return row;
                        }
                        return null;
                    }
                    throw new UnsupportedTypeException();
                }).filter(Objects::nonNull)).toList();

        var remainder = indexedData.stream()
                .filter(pair -> !joinedRowIndexes.contains(pair.getLeft()))
                .map(pair -> ListUtils.union(ModelUtils.emptyRow(table), pair.getRight()))
                .toList();

        return new Table(table.name() + joinedTable.name(), columns, ListUtils.union(joinedData, remainder), List.of());
    }

    @Override
    public void validate(Table table) {
        var joinedTable = projection.getByName(this.table);
        expression.accept(new ExpressionValidator(ListUtils.union(table.columns(), joinedTable.columns())));
    }
}