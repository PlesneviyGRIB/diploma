package com.core.sqlTool.model.index;

import com.core.sqlTool.model.domain.Column;

import java.util.List;

public record HashIndex(String name, List<Column> columns) implements Index {

    @Override
    public void construct() {

    }

}