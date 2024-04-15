package com.savchenko.sqlTool.model.resolver;

import com.savchenko.sqlTool.exception.ValidationException;
import com.savchenko.sqlTool.model.cache.CacheContext;
import com.savchenko.sqlTool.model.cache.CacheStrategy;
import com.savchenko.sqlTool.model.command.From;
import com.savchenko.sqlTool.model.command.domain.Command;
import com.savchenko.sqlTool.model.command.domain.ComplexCalculedCommand;
import com.savchenko.sqlTool.model.command.domain.SimpleCalculedCommand;
import com.savchenko.sqlTool.model.command.domain.SimpleCommand;
import com.savchenko.sqlTool.model.complexity.CachedCalculatorEntry;
import com.savchenko.sqlTool.model.complexity.Calculator;
import com.savchenko.sqlTool.model.domain.ExternalRow;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.query.Query;

import java.util.List;

public class Resolver {

    private final Projection projection;

    private final CacheContext cacheContext;

    public Resolver(Projection projection, CacheContext cacheContext) {
        this.projection = projection;
        this.cacheContext = cacheContext;
    }

    public ResolverResult resolve(Query query) {
        return resolve(query.build());
    }

    public ResolverResult resolve(List<Command> commands) {
        return resolve(commands, ExternalRow.empty());
    }

    public ResolverResult resolve(List<Command> commands, ExternalRow externalRow) {

        if (!commands.isEmpty()) {
            if (!(commands.get(0) instanceof From)) {
                throw new ValidationException("Query have to start with FROM statement!");
            }
        }

        Table table = new Table(null, null, null, externalRow);
        Calculator calculator = new Calculator();

        for (int i = 0; i < commands.size(); i++) {

            var command = commands.get(i);
            var tableRef = table;
            var cachePattern = commands.subList(0, i + 1);
            var cachedResult = cacheContext.get(cachePattern, externalRow);

            if (cachedResult.isPresent()) {
                var commandResult = cachedResult.get();
                calculator.log(new CachedCalculatorEntry(commandResult.calculatorEntry()));
                table = commandResult.table();
                continue;
            }

            var commandResult = command.accept(new Command.Visitor<CommandResult>() {

                @Override
                public CommandResult visit(SimpleCommand command) {
                    return command.run(tableRef, projection);
                }

                @Override
                public CommandResult visit(SimpleCalculedCommand command) {
                    return command.run(tableRef, projection);
                }

                @Override
                public CommandResult visit(ComplexCalculedCommand command) {
                    return command.run(tableRef, projection, Resolver.this);
                }

            });

            cacheContext.cacheCommand(cachePattern, externalRow, commandResult);
            calculator.log(commandResult.calculatorEntry());
            table = commandResult.table();

        }

        return new ResolverResult(table, calculator);
    }

    public Resolver utilityInstance() {
        return new Resolver(projection, new CacheContext(CacheStrategy.NONE));
    }
}
