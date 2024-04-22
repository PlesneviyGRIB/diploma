package com.savchenko.sqlTool.model.domain;

import com.savchenko.sqlTool.model.expression.Value;

import java.util.List;
import java.util.stream.Stream;

public record Table(String name, List<Column> columns, Stream<List<Value<?>>> dataStream, ExternalRow externalRow) {
}
