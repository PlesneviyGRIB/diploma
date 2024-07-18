package com.client.sqlTool;

import com.client.sqlTool.command.Group;
import com.client.sqlTool.command.Order;
import com.client.sqlTool.domain.Aggregation;
import com.client.sqlTool.domain.Column;
import com.client.sqlTool.expression.Binary;
import com.client.sqlTool.expression.Bool;
import com.client.sqlTool.expression.Number;
import com.client.sqlTool.query.Query;

import static com.client.sqlTool.expression.Operator.AND;
import static com.client.sqlTool.expression.Operator.EQ;

public class Main {
    public static void main(String[] args) {
        Query.from("asd").as("ads")
                .where(Binary.of(AND, Binary.of(EQ, Column.of("", ""), Number.of(10)), Bool.FALSE))
                .groupBy(
                        Group.of(Column.of("sdf", "asd"), Aggregation.MIN),
                        Group.of(Column.of("sdf", "asd"), Aggregation.MIN)
                )
                .orderBy(
                        Order.of(Column.of("", "Asd")),
                        Order.of(Column.of("", "Asd")).desc()).select(Column.of("", "sad")
                );
    }
}
