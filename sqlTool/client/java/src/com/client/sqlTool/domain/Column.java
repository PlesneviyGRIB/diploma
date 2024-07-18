package com.client.sqlTool.domain;

import com.client.sqlTool.expression.Expression;
import lombok.Getter;

@Getter
public class Column implements Expression {

    private final String table;

    private final String column;

    private Column(String table, String column) {
        this.table = table;
        this.column = column;
    }

    public static Column of(String table, String column) {
        return new Column(table, column);
    }
}
