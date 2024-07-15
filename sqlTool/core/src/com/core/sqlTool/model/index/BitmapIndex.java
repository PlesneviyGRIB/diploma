package com.core.sqlTool.model.index;

import com.core.sqlTool.model.domain.Column;

import java.util.List;

public class BitmapIndex extends Index {
    public BitmapIndex(String name, List<Column> columns) {
        super(name, columns);
    }

    @Override
    public void construct() {

    }
}
