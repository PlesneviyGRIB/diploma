package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.exception.UnsupportedTypeException;
import com.savchenko.sqlTool.model.expression.BooleanValue;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.expression.visitor.ExpressionCalculator;
import com.savchenko.sqlTool.model.expression.visitor.ExpressionValidator;
import com.savchenko.sqlTool.model.expression.visitor.ValueInjector;
import com.savchenko.sqlTool.model.structure.Table;
import com.savchenko.sqlTool.query.Query;
import com.savchenko.sqlTool.query.QueryResolver;
import com.savchenko.sqlTool.repository.Projection;
import com.savchenko.sqlTool.utils.ModelUtils;
import org.apache.commons.collections4.ListUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class LeftJoin extends Join {
    public LeftJoin(List<Command> commands, Expression<?> expression, JoinStrategy strategy, Projection projection) {
        super(commands, expression, strategy, projection);
    }

    @Override
    public Table run(Table table, Table joinedTable) {
        var columns = ListUtils.union(table.columns(), joinedTable.columns());

        var joinedRowIndexes = new HashSet<Integer>();
        var indexedData = ModelUtils.getIndexedData(table.data());

        var joinedData = indexedData.stream()
                .flatMap(pair -> joinedTable.data().stream().map(row2 -> {
                    var row = ListUtils.union(pair.getRight(), row2);

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
                }).filter(Objects::nonNull).toList().stream()).toList();

        var remainder = indexedData.stream()
                .filter(pair -> !joinedRowIndexes.contains(pair.getLeft()))
                .map(pair -> ListUtils.union(pair.getRight(), ModelUtils.emptyRow(joinedTable)))
                .toList();

        return new Table(null, columns, ListUtils.union(joinedData, remainder), List.of());
    }

}
