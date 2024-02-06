package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.exception.UnsupportedTypeException;
import com.savchenko.sqlTool.model.expression.BooleanValue;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.expression.NullValue;
import com.savchenko.sqlTool.model.expression.visitor.ExpressionCalculator;
import com.savchenko.sqlTool.model.expression.visitor.ExpressionValidator;
import com.savchenko.sqlTool.model.expression.visitor.ValueInjector;
import com.savchenko.sqlTool.model.structure.Column;
import com.savchenko.sqlTool.model.structure.Table;
import com.savchenko.sqlTool.repository.Projection;
import com.savchenko.sqlTool.utils.ModelUtils;
import org.apache.commons.collections4.ListUtils;

import java.util.List;
import java.util.Objects;

public class InnerJoin implements Command {
    private final String table;
    private final Expression<?> expression;

    public InnerJoin(String table, Expression<?> expression) {
        this.table = table;
        this.expression = expression;
    }

    @Override
    public Table run(Table table, Projection projection) {
        var joinedTable = projection.getByName(this.table);
        var columns = ListUtils.union(table.columns(), joinedTable.columns());

        var data = table.data().stream()
                .flatMap(row1 -> joinedTable.data().stream().map(row2 -> {
                    var row = ListUtils.union(row1, row2);
                    var o = new Object() { public boolean nullPresents; };
                    var value = expression
                            .accept(new ValueInjector(List.of(), List.of()) {
                                @Override
                                public Expression<?> visit(Column column) {
                                    var index = ModelUtils.resolveColumnIndex(columns, column);
                                    if(row.get(index) instanceof NullValue){
                                        o.nullPresents = true;
                                    }
                                    return column;
                                }
                            })
                            .accept(new ValueInjector(columns, row))
                            .accept(new ExpressionCalculator());

                    if(value instanceof BooleanValue bv) {
                        return bv.value() && !o.nullPresents ? row : null;
                    }
                    throw new UnsupportedTypeException();
                }).filter(Objects::nonNull)).toList();

        return new Table(table.name() + joinedTable.name(), columns, data);
    }

    @Override
    public void validate(Table table, Projection projection) {
        var joinedTable = projection.getByName(this.table);
        expression.accept(new ExpressionValidator(ListUtils.union(table.columns(), joinedTable.columns())));
    }


}
