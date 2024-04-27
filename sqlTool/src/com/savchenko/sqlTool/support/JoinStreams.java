package com.savchenko.sqlTool.support;

import com.savchenko.sqlTool.model.domain.Row;

import java.util.stream.Stream;

public record JoinStreams(Stream<Row> inner, Stream<Row> leftRemainder, Stream<Row> rightRemainder) {
}
