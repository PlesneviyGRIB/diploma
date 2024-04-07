package com.savchenko.sqlTool;

import com.savchenko.sqlTool.model.command.join.JoinStrategy;
import com.savchenko.sqlTool.model.expression.BooleanValue;
import com.savchenko.sqlTool.model.expression.LongNumber;
import com.savchenko.sqlTool.model.expression.StringValue;
import com.savchenko.sqlTool.model.expression.SubTable;
import com.savchenko.sqlTool.model.resolver.Resolver;
import com.savchenko.sqlTool.query.Q;
import com.savchenko.sqlTool.query.Query;
import com.savchenko.sqlTool.utils.DatabaseReader;
import com.savchenko.sqlTool.utils.printer.CalculatorPrinter;
import com.savchenko.sqlTool.utils.printer.TablePrinter;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import static com.savchenko.sqlTool.config.Constants.*;
import static com.savchenko.sqlTool.model.operator.Operator.*;

public class Main {

    public static void main(String[] args) throws SQLException {
        var connection = DriverManager.getConnection(String.format("jdbc:%s://localhost:%s/%s", DB_DRIVER, DB_PORT, DB_NAME), DB_USER, DB_PASSWORD);
        var projection = new DatabaseReader(connection).read();


        var query = new Query()
                .from("actions")
                .fullJoin(new Query().from("content"), new BooleanValue(true), JoinStrategy.LOOP)
                .as("r")
                .where(Q.op(AND
                        , Q.op(
                                LESS_OR_EQ,
                                Q.column("r", "actions.id"),
                                Q.op(
                                        MULTIPLY,
                                        new LongNumber(1L),
                                        new LongNumber(1042L)
                                )
                        ),
                        Q.op(EQ, Q.column("r", "action_id"), new StringValue("addRow")))
                )
                .limit(2);

        var predicate = Q.op(AND,
                Q.op(EQ, Q.op(MINUS, Q.column("c1", "id"), new LongNumber(2L)), Q.column("c1", "id")),
                Q.op(EQ, Q.column("c1", "id"), Q.op(MINUS, Q.column("c2", "id"), new LongNumber(4L)))
        );

        query = new Query()
                .from("courses")
                .as("c")
                .where(Q.op(EXISTS, new SubTable(
                        new Query()
                                .from("courses")
                                .as("c1")
                                .where(Q.op(OR,
                                        Q.op(EXISTS, new SubTable(
                                        new Query()
                                                .from("courses")
                                                .as("c2")
                                                .where(Q.op(GREATER, Q.column("c2", "id"), new LongNumber(152L)))
                                                .where(predicate)
                                                .build())),
                                        Q.op(NOT,
                                                Q.op(EXISTS, new SubTable(
                                                        new Query()
                                                                .from("courses")
                                                                .as("c2")
                                                                .where(Q.op(GREATER, Q.column("c2", "id"), new LongNumber(152L)))
                                                                .where(predicate)
                                                                .build()))

                                                )
                                        )
                                )
                                .build()))
                )
                .orderBy(Map.of(Q.column("c", "id"), false))
                .select(Q.column("c", "id"));


        var resolverResult = new Resolver(projection).resolve(query);
        var tableStr = new TablePrinter(resolverResult.table()).stringify();
        var calculatorStr = new CalculatorPrinter(resolverResult.calculator()).stringify();

        System.out.println(tableStr);
        System.out.println();
        System.out.println(calculatorStr);
    }
}