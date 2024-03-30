package com.savchenko.sqlTool;

import com.savchenko.sqlTool.model.Resolver;
import com.savchenko.sqlTool.model.command.join.JoinStrategy;
import com.savchenko.sqlTool.model.expression.BooleanValue;
import com.savchenko.sqlTool.model.expression.LongNumber;
import com.savchenko.sqlTool.model.expression.StringValue;
import com.savchenko.sqlTool.query.Q;
import com.savchenko.sqlTool.query.Query;
import com.savchenko.sqlTool.utils.DatabaseReader;
import com.savchenko.sqlTool.utils.TablePrinter;

import java.sql.DriverManager;
import java.sql.SQLException;

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
                .where(
                        Q.op(
                                LESS_OR_EQ,
                                Q.column("r", "actions.id"),
                                Q.op(
                                        MULTIPLY,
                                        new LongNumber(1L),
                                        new LongNumber(1042L)
                                )
                        ),
                        Q.op(EQ, Q.column("r", "action_id"), new StringValue("addRow"))
                );


        var resTable = new Resolver(projection).resolve(query);
        var resStr = new TablePrinter(resTable).stringify();

        System.out.println(resStr);
    }
}