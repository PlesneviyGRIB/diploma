package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.expression.Value;
import com.savchenko.sqlTool.model.structure.Column;
import com.savchenko.sqlTool.model.structure.Table;
import com.savchenko.sqlTool.repository.Projection;
import com.savchenko.sqlTool.utils.ModelUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Select implements Command {
    private final List<Column> columns;

    public Select(List<Column> columns) {
        this.columns = columns;
    }
    public Select() {
        this.columns = null;
    }

    @Override
    public Table run(Table table, Projection projection) {
        if(Objects.isNull(columns)){
            return table;
        }
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
        return new Table(table.name(), targetColumns, data);
    }

    @Override
    public void validate(Table table, Projection projection) {
        var contextColumns = table.columns();
        Optional.ofNullable(columns).orElse(List.of())
                .forEach(column -> ModelUtils.resolveColumn(contextColumns, column));
    }

}
