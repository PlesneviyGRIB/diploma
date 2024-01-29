package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.structure.Table;
import com.savchenko.sqlTool.utils.SqlUtils;
import com.savchenko.sqlTool.repository.Projection;
import com.savchenko.sqlTool.utils.ModelUtils;
import org.apache.commons.collections4.ListUtils;

import java.util.List;
import java.util.stream.Collectors;


public class From implements Command {
    private final List<String> tableNames;

    public From(List<String> tableNames) {
        this.tableNames = tableNames;
    }

    @Override
    public Table run(Table table, Projection projection) {
        var tables = tableNames.stream().map(projection::getByName).toList();
        var all = table.isEmpty() ? tables : ListUtils.union(List.of(table), tables);
        var tableName = all.stream().map(Table::name).collect(Collectors.joining("_"));
        if (all.isEmpty()){
            return table;
        }
        Table res = all.get(0);
        for (Table t: all.subList(1, all.size())){
            res = SqlUtils.cartesianProduct(res, t);
        }
        return ModelUtils.renameTable(res, tableName);
    }

    @Override
    public void validate(Table table, Projection projection) {
         tableNames.forEach(projection::getByName);
    }
}
