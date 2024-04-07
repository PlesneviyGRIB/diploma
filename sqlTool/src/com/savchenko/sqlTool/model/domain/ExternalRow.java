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
        return this;
//        if(Objects.nonNull(externalRow1) && Objects.nonNull(externalRow2)) {
//            return new ExternalRow(externalRow1.deepCopy(), externalRow2.deepCopy());
//        }
//
//        Set<Map.Entry<Column, Value<?>>> entries = this.columnValueMap.entrySet();
//
//        var columns = entries.stream().map(Map.Entry::getKey).toList();
//        var values = entries.stream().map(Map.Entry::getValue).collect(Collectors.<Value<?>>toList());
//
//        return new ExternalRow(columns, values);
    }

}
