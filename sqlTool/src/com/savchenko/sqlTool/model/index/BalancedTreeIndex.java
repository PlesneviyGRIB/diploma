package com.savchenko.sqlTool.model.index;

import com.savchenko.sqlTool.model.structure.Column;

import java.util.List;

public class BalancedTreeIndex extends Index {
    public BalancedTreeIndex(String name, List<Column> columns, boolean forUniqueValues) {
        super(name, columns, forUniqueValues);
    }

    @Override
    public void construct() {

    }
}
