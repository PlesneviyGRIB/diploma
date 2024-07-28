package com.core.sqlTool.model.domain;

import com.core.sqlTool.model.expression.Value;
import com.core.sqlTool.utils.ModelUtils;

import java.util.List;
import java.util.Optional;

public record HeaderRow(List<Column> columns, Row row) {

    public Optional<Value<?>> getValue(Column column) {
        return ModelUtils.columnIndex(columns, column).map(index -> row.values().get(index));
    }

    public static HeaderRow empty() {
        return new HeaderRow(List.of(), Row.empty());
    }

}
