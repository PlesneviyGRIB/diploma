package com.savchenko.sqlTool.model.domain;

import com.savchenko.sqlTool.model.expression.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

public class HeaderRow {
    protected final Map<Column, Value<?>> columnValueMap;

    public HeaderRow(List<Column> columns, List<Value<?>> values) {
        this.columnValueMap = new HashMap<>();
        IntStream.range(0, columns.size()).forEach(i -> columnValueMap.put(columns.get(i), values.get(i)));
    }

    public Optional<Value<?>> getValue(Column column) {
        return Optional.ofNullable(columnValueMap.get(column));
    }

}
