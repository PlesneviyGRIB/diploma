package com.savchenko.sqlTool.query;

import com.savchenko.sqlTool.exception.ValidationException;
import com.savchenko.sqlTool.model.command.From;
import com.savchenko.sqlTool.model.structure.Table;
import com.savchenko.sqlTool.model.command.Command;
import com.savchenko.sqlTool.repository.Projection;
import com.savchenko.sqlTool.utils.ModelUtils;

import java.util.List;

public class QueryResolver {

    public QueryResolver() {
    }

    public Table resolve(Query query) {
        var table = new Table("", List.of(), List.of(), List.of());
        var commands = query.build();
        commands.stream()
                .filter(command -> command instanceof From)
                .findAny()
                .orElseThrow(() -> new ValidationException("Query have to include FROM statement!"));
        for (Command cmd: commands) {
            cmd.validate(table);
            table = cmd.run(table);
        }
        return ModelUtils.renameTable(table, "result");
    }
}
