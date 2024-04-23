package com.savchenko.sqlTool.model.domain;

import com.savchenko.sqlTool.model.expression.Value;

import java.util.List;

public record Table(String name, List<Column> columns, List<List<Value<?>>> data) {
}
