package com.core.sqlTool.model.domain;

import org.apache.commons.collections4.ListUtils;

import java.util.List;

public class ExternalHeaderRow extends HeaderRow {

    public ExternalHeaderRow(List<Column> columns, Row row) {
        super(columns, row);
    }
    public static ExternalHeaderRow empty() {
        return new ExternalHeaderRow(List.of(), new Row(List.of()));
    }

    public ExternalHeaderRow merge(ExternalHeaderRow row) {
        return new ExternalHeaderRow(ListUtils.union(this.getColumns(), row.getColumns()), new Row(ListUtils.union(this.getRow().values(), row.getRow().values())));
    }

}
