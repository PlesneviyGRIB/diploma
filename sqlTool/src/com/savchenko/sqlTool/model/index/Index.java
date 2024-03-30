package com.savchenko.sqlTool.model.index;

import com.savchenko.sqlTool.model.domain.Column;

import java.util.List;

public abstract class Index {

    private final String name;

    private final List<Column> columns;

    public Index(String name, List<Column> columns) {
        this.name = name;
        this.columns = columns;
    }

    public abstract void construct();

    public String getName() {
        return name;
    }

    public List<Column> getColumns() {
        return columns;
    }

}
