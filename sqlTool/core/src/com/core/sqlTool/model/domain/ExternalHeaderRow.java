package com.core.sqlTool.model.domain;

import com.core.sqlTool.model.expression.Value;
import com.core.sqlTool.utils.ModelUtils;
import org.apache.commons.collections4.ListUtils;

import java.util.List;
import java.util.Optional;

public record ExternalHeaderRow(List<Column> columns, Row row) {

    public Optional<Value<?>> getValue(Column column) {
        return ModelUtils.columnIndex(columns, column).map(index -> row.values().get(index));
    }

    public static ExternalHeaderRow empty() {
        return new ExternalHeaderRow(List.of(), new Row(List.of()));
    }

    public ExternalHeaderRow merge(ExternalHeaderRow externalHeaderRow) {
        var columns = ListUtils.union(this.columns, externalHeaderRow.columns);
        var values = ListUtils.union(this.row.values(), externalHeaderRow.row.values());
        return new ExternalHeaderRow(columns, new Row(values));
    }

}
