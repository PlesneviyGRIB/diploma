package com.core.sqlTool.model.domain;

import com.core.sqlTool.model.expression.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

public class HeaderRow {

    private final Map<Column, Value<?>> columnValueMap;

    private final List<Column> columns;

    private final Row row;

    public List<Column> getColumns() {
        return columns;
    }

    public Row getRow() {
        return row;
    }

    public HeaderRow(List<Column> columns, Row row) {
        this.columnValueMap = new HashMap<>();
        IntStream.range(0, columns.size()).forEach(i -> columnValueMap.put(columns.get(i), row.values().get(i)));
        this.columns = columns;
        this.row = row;
    }

    public Optional<Value<?>> getValue(Column column) {
        return Optional.ofNullable(columnValueMap.get(column));
    }

    public static HeaderRow empty() {
        return new HeaderRow(List.of(), Row.empty());
    }

}
