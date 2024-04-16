package com.savchenko.sqlTool.model.resolver;

import com.savchenko.sqlTool.exception.ValidationException;
import com.savchenko.sqlTool.model.cache.CacheContext;
import com.savchenko.sqlTool.model.cache.CacheKey;
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
import java.util.Objects;

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

        var table = new Table(null, null, null, externalRow);
        var calculator = new Calculator();

        for (int i = 0; i < commands.size(); i++) {

            var command = commands.get(i);
            var commandSequence = commands.subList(0, i + 1);
            var cacheKey = new CacheKey(commandSequence, externalRow);

            //cache
            var cachedCommandResult = cacheContext.get(cacheKey).orElse(null);
            if (Objects.nonNull(cachedCommandResult)) {

                var cachedCalculatorEntry = cachedCommandResult.calculatorEntry();
                var cachedTable = cachedCommandResult.table();

                calculator.log(new CachedCalculatorEntry(cachedCalculatorEntry));

                if (Objects.nonNull(cachedTable)) {
                    table = cachedCommandResult.table();
                    continue;
                }
            }

            //execution
            var commandResult = run(command, table);

            cacheContext.put(cacheKey, commandResult);
            if (Objects.isNull(cachedCommandResult)) {
                calculator.log(commandResult.calculatorEntry());
            }
            table = commandResult.table();

        }

        return new ResolverResult(table, calculator);
    }

    private CommandResult run(Command command, Table table) {
        return command.accept(new Command.Visitor<>() {

            @Override
            public CommandResult visit(SimpleCommand command) {
                return command.run(table, projection);
            }

            @Override
            public CommandResult visit(SimpleCalculedCommand command) {
                return command.run(table, projection);
            }

            @Override
            public CommandResult visit(ComplexCalculedCommand command) {
                return command.run(table, projection, Resolver.this);
            }

        });
    }

    public Resolver utilityInstance() {
        return new Resolver(projection, new CacheContext(CacheStrategy.NONE));
    }
}
