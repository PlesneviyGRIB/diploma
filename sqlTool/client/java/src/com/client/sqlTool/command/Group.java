package com.client.sqlTool.command;

import com.client.sqlTool.domain.Aggregation;
import com.client.sqlTool.domain.Column;

public class Group {

    private final Column column;

    private final Aggregation aggregation;

    private Group(Column column, Aggregation aggregation) {
        this.column = column;
        this.aggregation = aggregation;
    }

    public static Group of(Column column, Aggregation aggregation) {
        return new Group(column, aggregation);
    }

}
