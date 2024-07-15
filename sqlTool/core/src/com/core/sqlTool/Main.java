package com.core.sqlTool;

import com.core.sqlTool.config.Constants;
import com.core.sqlTool.utils.DatabaseReader;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {
        var connection = DriverManager.getConnection(String.format("jdbc:%s://localhost:%s/%s", Constants.DB_DRIVER, Constants.DB_PORT, Constants.DB_NAME), Constants.DB_USER, Constants.DB_PASSWORD);
        var projection = new DatabaseReader(connection).read();


//        var query = Query.from("courses").as("c")
//                .select(Q.column("c", "id"))
//                .limit(1)
//                ;
//
//
//        var cacheContext = new CacheContext(CacheStrategy.NONE);
//        var resolverResult = new Resolver(projection, cacheContext).resolve(query);
//        var table = resolverResult.lazyTable().fetch();
//        var tableStr = new TablePrinter(table).stringify();
//        var calculatorStr = new CalculatorPrinter(resolverResult.calculator()).stringify();
//
//        System.out.println(tableStr);
//        System.out.println();
//        System.out.println(calculatorStr);
    }
}