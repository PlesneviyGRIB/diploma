package com.savchenko.sqlTool;

import com.savchenko.sqlTool.model.cache.CacheContext;
import com.savchenko.sqlTool.model.cache.CacheStrategy;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.model.expression.LongNumber;
import com.savchenko.sqlTool.model.resolver.Resolver;
import com.savchenko.sqlTool.query.Q;
import com.savchenko.sqlTool.query.Query;
import com.savchenko.sqlTool.utils.DatabaseReader;
import com.savchenko.sqlTool.utils.printer.CalculatorPrinter;
import com.savchenko.sqlTool.utils.printer.TablePrinter;

import java.sql.DriverManager;
import java.sql.SQLException;

import static com.savchenko.sqlTool.config.Constants.*;
import static com.savchenko.sqlTool.model.operator.Operator.*;

public class Main {

    public static void main(String[] args) throws SQLException {
        var connection = DriverManager.getConnection(String.format("jdbc:%s://localhost:%s/%s", DB_DRIVER, DB_PORT, DB_NAME), DB_USER, DB_PASSWORD);
        var projection = new DatabaseReader(connection).read();

        var query = new Query()
                .from("math_elements")
                .where(Q.op(GREATER, Q.column("math_elements", "id"), new LongNumber(10L)))
                .limit(10);


        var cacheContext = new CacheContext(CacheStrategy.PROPER);
        var resolverResult = new Resolver(projection, cacheContext).resolve(query);
        var resultLazyTable = resolverResult.lazyTable();
        var resultTable = new Table(resultLazyTable.name(), resultLazyTable.columns(), resultLazyTable.dataStream().toList());
        var tableStr = new TablePrinter(resultTable).stringify();
        var calculatorStr = new CalculatorPrinter(resolverResult.calculator()).stringify();

        System.out.println(tableStr);
        System.out.println();
        System.out.println(calculatorStr);
    }
}