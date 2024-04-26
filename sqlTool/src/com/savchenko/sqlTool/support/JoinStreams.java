package com.savchenko.sqlTool.support;

import com.savchenko.sqlTool.model.expression.Value;

import java.util.List;
import java.util.stream.Stream;

public record JoinStreams(Stream<List<Value<?>>> inner, Stream<List<Value<?>>> leftRemainder, Stream<List<Value<?>>> rightRemainder) {
}
