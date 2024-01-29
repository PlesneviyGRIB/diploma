package com.savchenko.sqlTool.model.structure;

import com.savchenko.sqlTool.model.expression.Value;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

public record Table(String name, List<Column> columns, List<List<Value<?>>> data) {
    public boolean isEmpty() {
        return this.data.isEmpty();
    }
}
