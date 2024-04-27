package com.savchenko.sqlTool.model.domain;

import java.util.List;

public record Table(String name, List<Column> columns, List<Row> data) {
}
