package com.core.sqlTool.model.index;

import com.core.sqlTool.model.domain.Column;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class Index {

    private final String name;

    private final List<Column> columns;

    public Index(String name, List<Column> columns) {
        this.name = name;
        this.columns = columns;
    }

    public abstract void construct();

}
