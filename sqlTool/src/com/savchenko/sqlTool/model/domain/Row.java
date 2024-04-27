package com.savchenko.sqlTool.model.domain;

import com.savchenko.sqlTool.model.expression.Value;
import org.apache.commons.collections4.ListUtils;

import java.util.ArrayList;
import java.util.List;

public record Row(List<Value<?>> values) {

    public Row() {
        this(new ArrayList<>());
    }

    public static Row merge(Row row1, Row row2) {
        return new Row(ListUtils.union(row1.values(), row2.values()));
    }

}
