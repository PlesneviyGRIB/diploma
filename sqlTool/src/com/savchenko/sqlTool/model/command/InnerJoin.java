package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.exception.UnsupportedTypeException;
import com.savchenko.sqlTool.model.expression.BooleanValue;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.visitor.ExpressionCalculator;
import com.savchenko.sqlTool.model.visitor.ValueInjector;
import com.savchenko.sqlTool.model.structure.Table;
import com.savchenko.sqlTool.repository.Projection;
import org.apache.commons.collections4.ListUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InnerJoin extends Join {
    public InnerJoin(List<Command> commands, Expression expression, JoinStrategy strategy, Projection projection) {
        super(commands, expression, strategy, projection);
    }

    @Override
    public Table run(Table table, Table joinedTable) {
        var columns = ListUtils.union(table.columns(), joinedTable.columns());

        var data = table.data().stream()
                .flatMap(row1 -> joinedTable.data().stream().map(row2 -> {
                    var row = ListUtils.union(row1, row2);

                    if(expressionContainNull(columns, row)) {
                        return null;
                    }

                    var value = expression
                            .accept(new ValueInjector(columns, row, Map.of()))
                            .accept(new ExpressionCalculator());

                    if(value instanceof BooleanValue bv) {
                        return bv.value() ? row : null;
                    }
                    throw new UnsupportedTypeException();
                }).filter(Objects::nonNull)).toList();

        return new Table(null, columns, data, List.of());
    }

}
