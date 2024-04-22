package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.command.domain.SimpleCommand;
import com.savchenko.sqlTool.model.complexity.SimpleEntry;
import com.savchenko.sqlTool.model.complexity.laziness.LazinessIndependent;
import com.savchenko.sqlTool.model.domain.Column;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.model.expression.Value;
import com.savchenko.sqlTool.model.resolver.CommandResult;
import com.savchenko.sqlTool.utils.ModelUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Select implements SimpleCommand, LazinessIndependent {

    private final List<Column> columns;

    public Select(List<Column> columns) {
        this.columns = columns;
    }

    @Override
    public CommandResult run(Table table, Projection projection) {

        var contextColumns = table.columns();
        var indexes = columns.stream().map(c -> ModelUtils.resolveColumnIndex(contextColumns, c)).toList();
        var data = table.dataStream()
                .map(l -> {
                    List<Value<?>> list = new ArrayList<>(indexes.size());
                    for (Integer index : indexes) {
                        list.add(l.get(index));
                    }
                    return list;
                });

        var targetColumns = indexes.stream().map(contextColumns::get).toList();

        return new CommandResult(
                new Table(table.name(), targetColumns, data, table.externalRow()),
                new SimpleEntry(this)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Select select = (Select) o;
        return Objects.equals(columns, select.columns);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(columns);
    }
}
