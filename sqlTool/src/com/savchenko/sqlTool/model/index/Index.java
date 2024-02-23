package com.savchenko.sqlTool.model.index;

import com.savchenko.sqlTool.model.structure.Column;

import java.util.List;

public abstract class Index {
    private final String name;
    private final List<Column> columns;
    private final boolean forUniqueValues;

    public Index(String name, List<Column> columns, boolean forUniqueValues) {
        this.name = name;
        this.columns = columns;
        this.forUniqueValues = forUniqueValues;
    }

    public abstract void construct();

    public String getName() {
        return name;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public boolean isForUniqueValues() {
        return forUniqueValues;
    }
}
