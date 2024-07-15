package com.core.sqlTool.support;

import com.core.sqlTool.model.domain.Row;

import java.util.stream.Stream;

public record JoinStreams(Stream<Row> inner, Stream<Row> leftRemainder, Stream<Row> rightRemainder) {
}
