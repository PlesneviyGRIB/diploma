package com.savchenko.sqlTool.model.domain;

import com.savchenko.sqlTool.utils.ModelUtils;

import java.util.List;
import java.util.stream.Stream;

public record LazyTable(String name, List<Column> columns, Stream<Row> dataStream, ExternalHeaderRow externalRow) {

    public HeaderRow phonyHeaderRow() {
        return new HeaderRow(columns, ModelUtils.emptyRow(this));
    }

    public Table fetch() {
        return new Table(name, columns, dataStream.toList());
    }

}
