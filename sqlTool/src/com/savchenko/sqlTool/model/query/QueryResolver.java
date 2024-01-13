package com.savchenko.sqlTool.model.query;

import com.savchenko.sqlTool.model.Table;
import com.savchenko.sqlTool.model.operation.Operation;
import com.savchenko.sqlTool.repository.Projection;

public class QueryResolver {
    private final Projection projection;

    public QueryResolver(Projection projection) {
        this.projection = projection;
    }

    public Table resolve(Query query) {
        var table = new Table("result");
        var operations = query.build();

        for (Operation op: operations){
            table = op.run(table, projection);
        }

        return table;
    }
}
