package com.client.sqlTool.domain;

import com.client.sqlTool.expression.Expression;

public class Column implements Expression {

    private final String column;

    private Column(String column) {
        this.column = column;
    }

    public static Column of(String column) {
        return new Column(column);
    }
}
