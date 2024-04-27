package com.savchenko.sqlTool.model.domain;

import com.savchenko.sqlTool.model.expression.Value;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class ExternalHeaderRow extends HeaderRow {

    private final ExternalHeaderRow externalRow1;

    private final ExternalHeaderRow externalRow2;

    public ExternalHeaderRow(List<Column> columns, Row row) {
        super(columns, row);
        this.externalRow1 = null;
        this.externalRow2 = null;
    }

    public ExternalHeaderRow(ExternalHeaderRow externalRow1, ExternalHeaderRow externalRow2) {
        super(List.of(), new Row());
        this.externalRow1 = externalRow1;
        this.externalRow2 = externalRow2;
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

        return Optional.of(value);
    }

    public List<Column> getColumns() {
        return Stream.of(
                columnValueMap.keySet(),
                Optional.ofNullable(externalRow1).map(ExternalHeaderRow::getColumns).orElse(List.of()),
                Optional.ofNullable(externalRow2).map(ExternalHeaderRow::getColumns).orElse(List.of())
        ).flatMap(Collection::stream).toList();
    }

    public static ExternalHeaderRow empty() {
        return new ExternalHeaderRow(List.of(), new Row(List.of()));
    }

    public ExternalHeaderRow merge(ExternalHeaderRow row) {
        return new ExternalHeaderRow(this, row);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExternalHeaderRow that = (ExternalHeaderRow) o;
        return Objects.equals(externalRow1, that.externalRow1)
                && Objects.equals(externalRow2, that.externalRow2)
                && Objects.equals(columnValueMap, that.columnValueMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(externalRow1, externalRow2, columnValueMap);
    }
}
