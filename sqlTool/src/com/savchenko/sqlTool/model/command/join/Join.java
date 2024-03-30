package com.savchenko.sqlTool.model.command.join;

import com.savchenko.sqlTool.exception.ValidationException;
import com.savchenko.sqlTool.model.Resolver;
import com.savchenko.sqlTool.model.command.domain.Command;
import com.savchenko.sqlTool.model.command.domain.ComplicatedCalculedCommand;
import com.savchenko.sqlTool.model.complexity.Calculator;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.expression.Value;
import com.savchenko.sqlTool.model.visitor.ExpressionValidator;
import com.savchenko.sqlTool.utils.ModelUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static java.lang.String.format;

public abstract class Join extends ComplicatedCalculedCommand {

    private final List<Command> commands;

    private final JoinStrategy strategy;

    public Join(List<Command> commands, Expression expression, JoinStrategy strategy) {
        super(expression);
        this.commands = commands;
        this.strategy = strategy;
    }

    abstract Table run(Table table, Table joinedTable, Supplier<Triple<List<List<Value<?>>>, Set<Integer>, Set<Integer>>> strategyExecutionResultSupplier);

    @Override
    public Table run(Table table, Projection projection, Resolver resolver, Calculator calculator) {
        var joinedTable = resolver.resolve(commands);

        if (table.name().equals(joinedTable.name())) {
            throw new ValidationException("There are two tables with the same name '%s' in context. Use alias to resolve the conflict.", table.name());
        }

        var mergedColumns = ListUtils.union(table.columns(), joinedTable.columns());
        expression.accept(new ExpressionValidator(mergedColumns));

        Supplier<Triple<List<List<Value<?>>>, Set<Integer>, Set<Integer>>> strategyExecutionResultSupplier = () -> strategy.run(table, joinedTable, expression);

        var targetTable = run(table, joinedTable, strategyExecutionResultSupplier);
        var tableName = format("%s_%s", table.name(), joinedTable.name());

        return ModelUtils.renameTable(targetTable, tableName);

    }
}
