package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.command.domain.SimpleCommand;
import com.savchenko.sqlTool.model.domain.Column;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.model.expression.Value;
import com.savchenko.sqlTool.utils.ModelUtils;

import java.util.ArrayList;
import java.util.List;

public class Select implements SimpleCommand {
    private final List<Column> columns;

    public Select(List<Column> columns) {
        this.columns = columns;
    }

    @Override
    public Table run(Table table, Projection projection) {
        var contextColumns = table.columns();
        var indexes = columns.stream().map(c -> ModelUtils.resolveColumnIndex(contextColumns, c)).toList();
        var data = table.data().stream()
                .map(l -> {
                    List<Value<?>> list = new ArrayList<>(indexes.size());
                    for (Integer index : indexes) {
                        list.add(l.get(index));
                    }
                    return list;
                }).toList();
        var targetColumns = indexes.stream().map(contextColumns::get).toList();
        return new Table(table.name(), targetColumns, data, table.externalRow());
    }

}
