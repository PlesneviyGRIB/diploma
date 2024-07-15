package com.client.sqlTool;

import com.client.sqlTool.command.Group;
import com.client.sqlTool.command.Order;
import com.client.sqlTool.domain.Aggregation;
import com.client.sqlTool.domain.Column;
import com.client.sqlTool.query.Query;

public class Main {
    public static void main(String[] args) {
        Query.from("asd").as("ads")
                .groupBy(
                        Group.of(Column.of("asd"), Aggregation.MIN),
                        Group.of(Column.of("asd"), Aggregation.MIN)
                )
                .orderBy(
                        Order.of(Column.of("Asd")),
                        Order.of(Column.of("Asd")).desc()).select(Column.of("sad")
                );
    }
}
