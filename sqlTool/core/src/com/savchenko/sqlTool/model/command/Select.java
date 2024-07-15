package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.command.domain.SimpleCommand;
import com.savchenko.sqlTool.model.domain.Column;
import com.savchenko.sqlTool.model.domain.LazyTable;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Row;
import com.savchenko.sqlTool.model.expression.Value;
import com.savchenko.sqlTool.utils.ModelUtils;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Select implements SimpleCommand {

    private final List<Column> columns;

    public Select(List<Column> columns) {
        this.columns = columns;
    }

    @Override
    public LazyTable run(LazyTable lazyTable, Projection projection) {

        var contextColumns = lazyTable.columns();
        var columnIndexes = columns.stream().map(c -> ModelUtils.resolveColumnIndex(contextColumns, c)).toList();
        var targetColumns = columnIndexes.stream().map(contextColumns::get).toList();

        Function<Row, Row> mapper = row -> columnIndexes.stream()
                .map(index -> row.values().get(index))
                .collect(Collectors.collectingAndThen(Collectors.<Value<?>>toList(), Row::new));

        return new LazyTable(lazyTable.name(), targetColumns, lazyTable.dataStream().map(mapper), lazyTable.externalRow());
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
