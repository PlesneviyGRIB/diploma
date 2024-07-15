package com.client.sqlTool.expression;

import java.sql.Timestamp;

public class TimeStamp implements Expression {

    private final Timestamp timestamp;

    private TimeStamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public static TimeStamp of(Timestamp timestamp) {
        return new TimeStamp(timestamp);
    }

}
