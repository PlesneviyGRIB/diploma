package com.savchenko.sqlTool.model;

import java.util.List;

public record Table(String name, List<Column> columns, List<List<String>> data) {
    public boolean isEmpty() {
        return this.data.isEmpty();
    }
}
