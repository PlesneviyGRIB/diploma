package com.savchenko.sqlTool.model.operation;

import com.savchenko.sqlTool.model.Table;
import com.savchenko.sqlTool.repository.Projection;
import com.savchenko.sqlTool.supportive.Utils;
import org.apache.commons.collections4.ListUtils;

import java.util.List;
import java.util.stream.Collectors;


public class From implements Operation {
    private final List<String> tableNames;

    public From(List<String> tableNames) {
        this.tableNames = tableNames;
    }

    @Override
    public Table run(Table table, Projection projection) {
        var tables = tableNames.stream().map(projection::getByName).toList();
        var all = table.isEmpty() ? tables : ListUtils.union(List.of(table), tables);
        var tableName = all.stream().map(Table::getName).collect(Collectors.joining("_"));
        if (all.isEmpty()){
            return table;
        }
        Table res = all.get(0);
        for (Table t: all.subList(1, all.size())){
            res = OperationUtils.cartesianProduct(res, t);
        }
        return Utils.renameTable(res, tableName);
    }
}
