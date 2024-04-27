package com.savchenko.sqlTool;

import com.savchenko.sqlTool.model.cache.CacheContext;
import com.savchenko.sqlTool.model.cache.CacheStrategy;
import com.savchenko.sqlTool.model.command.join.JoinStrategy;
import com.savchenko.sqlTool.model.expression.BooleanValue;
import com.savchenko.sqlTool.model.resolver.Resolver;
import com.savchenko.sqlTool.query.Query;
import com.savchenko.sqlTool.utils.DatabaseReader;
import com.savchenko.sqlTool.utils.printer.CalculatorPrinter;
import com.savchenko.sqlTool.utils.printer.TablePrinter;

import java.sql.DriverManager;
import java.sql.SQLException;

import static com.savchenko.sqlTool.config.Constants.*;

public class Main {

    public static void main(String[] args) throws SQLException {
        var connection = DriverManager.getConnection(String.format("jdbc:%s://localhost:%s/%s", DB_DRIVER, DB_PORT, DB_NAME), DB_USER, DB_PASSWORD);
        var projection = new DatabaseReader(connection).read();

        var query = Query
                .from("content")
                .fullJoin(Query.from("content_descriptor"), new BooleanValue(true), JoinStrategy.LOOP);


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