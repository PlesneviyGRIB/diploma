package com.savchenko.sqlTool;

import com.savchenko.sqlTool.model.cache.CacheContext;
import com.savchenko.sqlTool.model.cache.CacheStrategy;
import com.savchenko.sqlTool.model.command.join.JoinStrategy;
import com.savchenko.sqlTool.model.expression.BooleanValue;
import com.savchenko.sqlTool.model.expression.LongNumber;
import com.savchenko.sqlTool.model.expression.SubTable;
import com.savchenko.sqlTool.model.operator.Operator;
import com.savchenko.sqlTool.model.resolver.Resolver;
import com.savchenko.sqlTool.query.Q;
import com.savchenko.sqlTool.query.Query;
import com.savchenko.sqlTool.utils.DatabaseReader;
import com.savchenko.sqlTool.utils.printer.CalculatorPrinter;
import com.savchenko.sqlTool.utils.printer.TablePrinter;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static com.savchenko.sqlTool.config.Constants.*;
import static com.savchenko.sqlTool.model.operator.Operator.*;
import static com.savchenko.sqlTool.model.operator.Operator.MINUS;

public class Main {

    public static void main(String[] args) throws SQLException {
        var connection = DriverManager.getConnection(String.format("jdbc:%s://localhost:%s/%s", DB_DRIVER, DB_PORT, DB_NAME), DB_USER, DB_PASSWORD);
        var projection = new DatabaseReader(connection).read();

        var query = Query
                .from("courses")
                .as("c")
                .where(Q.op(EXISTS, new SubTable(
                        Query
                                .from("courses")
                                .as("c1")
                                .where(Q.op(OR, Q.op(OR,
                                                        Q.op(EXISTS, new SubTable(
                                                                Query
                                                                        .from("courses")
                                                                        .as("c2")
                                                                        .where(Q.op(GREATER, Q.column("c2", "id"), new LongNumber(152L)))
                                                                        .where(Q.op(AND,
                                                                                Q.op(EQ, Q.op(MINUS, Q.column("c1", "id"), new LongNumber(2L)), Q.column("c1", "id")),
                                                                                Q.op(EQ, Q.column("c1", "id"), Q.op(MINUS, Q.column("c2", "id"), new LongNumber(4L)))
                                                                        ))
                                                                        .build())),
                                                        Q.op(NOT,
                                                                Q.op(EXISTS, new SubTable(
                                                                        Query
                                                                                .from("courses")
                                                                                .as("c2")
                                                                                .where(Q.op(GREATER, Q.column("c2", "id"), new LongNumber(152L)))
                                                                                .where(Q.op(AND,
                                                                                        Q.op(EQ, Q.op(MINUS, Q.column("c1", "id"), new LongNumber(2L)), Q.column("c1", "id")),
                                                                                        Q.op(EQ, Q.column("c", "id"), Q.op(MINUS, Q.column("c2", "id"), new LongNumber(4L)))
                                                                                ))
                                                                                .build()))
                                                        )
                                                ),
                                                Q.op(EXISTS, new SubTable(
                                                        Query
                                                                .from("courses")
                                                                .as("c2")
                                                                .where(Q.op(GREATER, Q.column("c2", "id"), new LongNumber(152L)))
                                                                .where(Q.op(AND,
                                                                        Q.op(EQ, Q.op(MINUS, Q.column("c1", "id"), new LongNumber(2L)), Q.column("c1", "id")),
                                                                        Q.op(EQ, Q.column("c1", "id"), Q.op(MINUS, Q.column("c2", "id"), new LongNumber(4L)))
                                                                ))
                                                                .build()))
                                        )
                                )
                                .build()))
                )
                //.orderBy(List.of(Pair.of(Q.column("c", "id"), false)))
                .select(Q.column("c", "id"))
                .limit(1)
                ;


        var cacheContext = new CacheContext(CacheStrategy.NONE);
        var resolverResult = new Resolver(projection, cacheContext).resolve(query);
        var table = resolverResult.lazyTable().fetch();
        var tableStr = new TablePrinter(table).stringify();
        var calculatorStr = new CalculatorPrinter(resolverResult.calculator()).stringify();

        System.out.println(tableStr);
        System.out.println();
        System.out.println(calculatorStr);
    }
}