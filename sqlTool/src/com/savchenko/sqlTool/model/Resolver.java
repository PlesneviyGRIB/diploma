package com.savchenko.sqlTool.model;

import com.savchenko.sqlTool.exception.ValidationException;
import com.savchenko.sqlTool.model.command.From;
import com.savchenko.sqlTool.model.command.domain.Command;
import com.savchenko.sqlTool.model.command.domain.ComplicatedCalculedCommand;
import com.savchenko.sqlTool.model.command.domain.SimpleCalculedCommand;
import com.savchenko.sqlTool.model.command.domain.SimpleCommand;
import com.savchenko.sqlTool.model.complexity.Calculator;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.query.Query;

import java.util.List;

public class Resolver {

    private Projection projection;

    public Resolver(Projection projection) {
        this.projection = projection;
    }

    public Table resolve(Query query) {
        return resolve(query.build());
    }

    public Table resolve(List<Command> commands) {

        if (!commands.isEmpty()) {
            if (!(commands.get(0) instanceof From)) {
                throw new ValidationException("Query have to start with FROM statement!");
            }
        }

        Table table = null;
        Calculator calculator = new Calculator();

        for (Command cmd : commands) {

            var tableRef = table;

            table = cmd.accept(new Command.Visitor<>() {

                @Override
                public Table visit(SimpleCommand command) {
                    return command.run(tableRef, projection);
                }

                @Override
                public Table visit(SimpleCalculedCommand command) {
                    return command.run(tableRef, projection, calculator);
                }

                @Override
                public Table visit(ComplicatedCalculedCommand command) {
                    return command.run(tableRef, projection, Resolver.this, calculator);
                }

            });
        }
        return table;
    }
}
