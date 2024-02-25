package com.savchenko.sqlTool.query;

import com.savchenko.sqlTool.exception.ValidationException;
import com.savchenko.sqlTool.model.command.CalculatedCommand;
import com.savchenko.sqlTool.model.command.Command;
import com.savchenko.sqlTool.model.command.From;
import com.savchenko.sqlTool.model.structure.Table;
import com.savchenko.sqlTool.model.command.SimpleCommand;
import com.savchenko.sqlTool.utils.ModelUtils;

import java.util.List;

public class QueryResolver {

    public QueryResolver() {
    }

    public Table resolve(Query query) {
        return resolve(query.build());
    }

    public Table resolve(List<Command> commands) {

        if(!commands.isEmpty()) {
            if(!(commands.get(0) instanceof From)) {
                throw new ValidationException("Query have to start with FROM statement!");
            }
        }

        Table table = null;

        for (Command cmd: commands) {
            cmd.validate(table);
            var tableRef = table;

            table = cmd.accept(new Command.Visitor<>() {

                @Override
                public Table visit(SimpleCommand command) {
                    return command.run(tableRef);
                }

                @Override
                public Table visit(CalculatedCommand command) {
                    return command.run(tableRef, QueryResolver.this);
                }
            });
        }
        return table;
    }
}
