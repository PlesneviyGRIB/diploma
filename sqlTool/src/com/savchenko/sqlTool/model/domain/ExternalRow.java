package com.savchenko.sqlTool.model.domain;

import com.savchenko.sqlTool.model.expression.Value;

import java.util.List;

public record ExternalRow(List<Column> columns, List<Value<?>> values) {

    public static ExternalRow empty() {
        return new ExternalRow(List.of(), List.of());
    }

}
