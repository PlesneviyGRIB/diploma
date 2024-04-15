package com.savchenko.sqlTool.model.domain;

import com.savchenko.sqlTool.model.expression.Value;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExternalRow extends Row {

    private final ExternalRow externalRow1;

    private final ExternalRow externalRow2;

    private boolean used;

    public ExternalRow(List<Column> columns, List<Value<?>> values) {
        super(columns, values);
        this.externalRow1 = null;
        this.externalRow2 = null;
    }

    public ExternalRow(ExternalRow externalRow1, ExternalRow externalRow2) {
        super(List.of(), List.of());
        this.externalRow1 = externalRow1;
        this.externalRow2 = externalRow2;
    }

    public boolean isUsed() {
        return used;
    }

    public Optional<Value<?>> getValue(Column column) {

        var value = columnValueMap.get(column);

        if (Objects.isNull(value) && Objects.nonNull(externalRow2)) {
            value = externalRow2.getValue(column).orElse(null);
        }

        if (Objects.isNull(value) && Objects.nonNull(externalRow1)) {
            value = externalRow1.getValue(column).orElse(null);
        }

        if (Objects.isNull(value)) {
            return Optional.empty();
        }

        this.used = true;

        return Optional.of(value);
    }

    public List<Column> getColumns() {
        return Stream.of(
                columnValueMap.keySet(),
                Optional.ofNullable(externalRow1).map(ExternalRow::getColumns).orElse(List.of()),
                Optional.ofNullable(externalRow2).map(ExternalRow::getColumns).orElse(List.of())
        ).flatMap(Collection::stream).toList();
    }

    public static ExternalRow empty() {
        return new ExternalRow(List.of(), List.of());
    }

    public ExternalRow merge(ExternalRow row) {
        return new ExternalRow(this, row);
    }

    public ExternalRow deepCopy() {
        if (Objects.nonNull(externalRow1)) {
            return new ExternalRow(externalRow1.deepCopy(), externalRow2.deepCopy());
        }

        var entries = columnValueMap.entrySet().stream().toList();
        var columns = entries.stream().map(Map.Entry::getKey).toList();
        List<Value<?>> values = entries.stream().map(Map.Entry::getValue).collect(Collectors.toList());

        return new ExternalRow(columns, values);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExternalRow that = (ExternalRow) o;
        return Objects.equals(externalRow1, that.externalRow1)
                && Objects.equals(externalRow2, that.externalRow2)
                && Objects.equals(columnValueMap, that.columnValueMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(externalRow1, externalRow2, columnValueMap);
    }
}
