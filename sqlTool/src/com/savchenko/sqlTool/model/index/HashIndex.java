package com.savchenko.sqlTool.model.index;

import com.savchenko.sqlTool.model.domain.Column;

import java.util.List;

public class HashIndex extends Index {
    public HashIndex(String name, List<Column> columns, boolean forUniqueValues) {
        super(name, columns, forUniqueValues);
    }

    @Override
    public void construct() {

    }
}
