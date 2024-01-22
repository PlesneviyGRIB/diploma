package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.Column;
import com.savchenko.sqlTool.model.Table;
import com.savchenko.sqlTool.repository.Projection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.String.format;

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
        var tableColumns = table.columns();
        columns.stream()
                .filter(c -> !tableColumns.contains(c))
                .findFirst()
                .ifPresent(column -> {
                    throw new RuntimeException(format("Unable to find column '%s' in context. There are only [%s]",
                            column, tableColumns.stream().map(Column::toString).collect(Collectors.joining(", "))));
                });
        var indexes = columns.stream().map(tableColumns::indexOf).toList();
        var data = table.data().stream()
                .map(l -> {
                    List<Comparable<?>> list = new ArrayList<>(indexes.size());
                    for (Integer index : indexes) {
                        list.add(l.get(index));
                    }
                    return list;
                }).toList();
        var targetColumns = indexes.stream().map(tableColumns::get).toList();
        return new Table(table.name(), targetColumns, data);
    }

    @Override
    public void validate(Projection projection) {
    }
}
