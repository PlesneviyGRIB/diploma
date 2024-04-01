package com.savchenko.sqlTool.model.resolver;

import com.savchenko.sqlTool.exception.ValidationException;
import com.savchenko.sqlTool.model.command.From;
import com.savchenko.sqlTool.model.command.domain.Command;
import com.savchenko.sqlTool.model.command.domain.ComplexCalculedCommand;
import com.savchenko.sqlTool.model.command.domain.SimpleCalculedCommand;
import com.savchenko.sqlTool.model.command.domain.SimpleCommand;
import com.savchenko.sqlTool.model.complexity.Calculator;
import com.savchenko.sqlTool.model.domain.ExternalRow;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.query.Query;

import java.util.List;

public class Resolver {

    private final Projection projection;

    public Resolver(Projection projection) {
        this.projection = projection;
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

        for (Command cmd : commands) {

            var tableRef = table;

            table = cmd.accept(new Command.Visitor<>() {

                @Override
                public Table visit(SimpleCommand command) {
                    return command.run(tableRef, projection, calculator);
                }

                @Override
                public Table visit(SimpleCalculedCommand command) {
                    return command.run(tableRef, projection, calculator);
                }

                @Override
                public Table visit(ComplexCalculedCommand command) {
                    return command.run(tableRef, projection, Resolver.this, calculator);
                }

            });

        }
        return new ResolverResult(table, calculator);
    }
}
