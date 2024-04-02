package com.savchenko.sqlTool.model.domain;

import com.savchenko.sqlTool.model.expression.Value;
import org.apache.commons.collections4.ListUtils;

import java.util.List;

public record ExternalRow(List<Column> columns, List<Value<?>> values) {

    public static ExternalRow empty() {
        return new ExternalRow(List.of(), List.of());
    }

    public ExternalRow merge(ExternalRow row) {
        return new ExternalRow(
                ListUtils.union(this.columns, row.columns),
                ListUtils.union(this.values, row.values)
        );
    }

}
