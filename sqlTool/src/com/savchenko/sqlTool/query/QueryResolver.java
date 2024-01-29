package com.savchenko.sqlTool.query;

import com.savchenko.sqlTool.model.structure.Table;
import com.savchenko.sqlTool.model.command.Command;
import com.savchenko.sqlTool.repository.Projection;
import com.savchenko.sqlTool.utils.ModelUtils;

import java.util.List;

public class QueryResolver {
    private final Projection projection;

    public QueryResolver(Projection projection) {
        this.projection = projection;
    }

    public Table resolve(Query query) {
        var table = new Table("", List.of(), List.of());
        for (Command cmd: query.build()) {
            cmd.validate(table, projection);
            table = cmd.run(table, projection);
        }
        return ModelUtils.renameTable(table, "result");
    }
}
