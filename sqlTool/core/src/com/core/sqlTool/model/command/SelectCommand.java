package com.core.sqlTool.model.command;

import com.core.sqlTool.model.command.domain.SimpleCommand;
import com.core.sqlTool.model.expression.Value;
import com.core.sqlTool.model.domain.Column;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.domain.Projection;
import com.core.sqlTool.model.domain.Row;
import com.core.sqlTool.utils.ModelUtils;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SelectCommand implements SimpleCommand {

    private final List<Column> columns;

    public SelectCommand(List<Column> columns) {
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
        SelectCommand select = (SelectCommand) o;
        return Objects.equals(columns, select.columns);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(columns);
    }
}
