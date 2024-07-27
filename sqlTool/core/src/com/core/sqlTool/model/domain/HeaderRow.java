package com.core.sqlTool.model.domain;

import com.core.sqlTool.model.expression.Value;
import com.core.sqlTool.utils.ModelUtils;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

@Getter
public class HeaderRow {

    private final List<Column> columns;

    private final Row row;

    public HeaderRow(List<Column> columns, Row row) {
        this.columns = columns;
        this.row = row;
    }

    public Optional<Value<?>> getValue(Column column) {

        var indexOpt = ModelUtils.columnIndex(columns, column);
        if (indexOpt.isEmpty()) {
            return Optional.empty();
        }

        var index = indexOpt.get();
        var value = row.values().get(index);

        return Optional.of(value);
    }

    public static HeaderRow empty() {
        return new HeaderRow(List.of(), Row.empty());
    }

}
