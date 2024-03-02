package com.savchenko.sqlTool.model.command.join;

import com.savchenko.sqlTool.exception.ValidationException;
import com.savchenko.sqlTool.model.command.CalculatedCommand;
import com.savchenko.sqlTool.model.command.Command;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.expression.NullValue;
import com.savchenko.sqlTool.model.expression.Value;
import com.savchenko.sqlTool.model.visitor.ExpressionTraversal;
import com.savchenko.sqlTool.model.visitor.ExpressionValidator;
import com.savchenko.sqlTool.model.domain.Column;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.model.Resolver;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.utils.ModelUtils;
import org.apache.commons.collections4.ListUtils;

import java.util.List;

import static java.lang.String.format;

public abstract class Join extends CalculatedCommand {

    protected final List<Command> commands;

    protected final JoinStrategy strategy;

    public Join(List<Command> commands, Expression expression, JoinStrategy strategy, Projection projection) {
        super(expression, projection);
        this.commands = commands;
        this.strategy = strategy;
    }

    abstract Table run(Table table, Table joinedTable);

    @Override
    public Table run(Table table, Resolver resolver) {
        var joinedTable = resolver.resolve(commands);

        if(table.name().equals(joinedTable.name())) {
            throw new ValidationException("There are two tables with the same name '%s' in context. Use alias to resolve the conflict.", table.name());
        }

        var columns = ListUtils.union(table.columns(), joinedTable.columns());

        expression.accept(new ExpressionValidator(columns));

        var targetTable = run(table, joinedTable);
        var tableName = format("%s_%s", table.name(), joinedTable.name());

        return ModelUtils.renameTable(targetTable, tableName);

    };

    protected boolean expressionContainNull(List<Column> columns, List<Value<?>> row) {
        var o = new Object() { public boolean nullPresents; };
        expression.accept(new ExpressionTraversal() {
            @Override
            public Void visit(Column column) {
                var index = ModelUtils.resolveColumnIndex(columns, column);
                if(row.get(index) instanceof NullValue){
                    o.nullPresents = true;
                }
                return null;
            }
        });
        return o.nullPresents;
    }
}
