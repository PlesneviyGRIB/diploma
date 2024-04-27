package com.savchenko.sqlTool.model.domain;

import java.util.List;
import java.util.stream.Stream;

public record LazyTable(String name, List<Column> columns, Stream<Row> dataStream, ExternalHeaderRow externalRow) {
}
