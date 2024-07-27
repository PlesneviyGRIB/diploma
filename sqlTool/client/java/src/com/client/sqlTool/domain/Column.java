package com.client.sqlTool.domain;

import com.client.sqlTool.expression.Expression;
import lombok.Getter;

@Getter
public class Column extends Expression {

    private final String columnName;

    private Column(String columnName) {
        this.columnName = columnName;
    }

    public static Column of(String name) {
        var column = new Column(name);
        column.as(name);
        return column;
    }

}
