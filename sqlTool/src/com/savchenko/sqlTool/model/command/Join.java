package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.expression.NullValue;
import com.savchenko.sqlTool.model.expression.Value;
import com.savchenko.sqlTool.model.expression.visitor.ValueInjector;
import com.savchenko.sqlTool.model.structure.Column;
import com.savchenko.sqlTool.utils.ModelUtils;

import java.util.List;

public abstract class Join implements Command {

    protected final String table;

    protected final Expression<?> expression;

    public Join(String table, Expression<?> expression) {
        this.table = table;
        this.expression = expression;
    }

    protected boolean expressionContainNull(List<Column> columns, List<Value<?>> row) {
        var o = new Object() { public boolean nullPresents; };
        expression.accept(new ValueInjector(List.of(), List.of()) {
            @Override
            public Expression<?> visit(Column column) {
                var index = ModelUtils.resolveColumnIndex(columns, column);
                if(row.get(index) instanceof NullValue){
                    o.nullPresents = true;
                }
                return column;
            }
        });
        return o.nullPresents;
    }
}
