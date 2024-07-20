package com.core.sqlTool;

import com.client.sqlTool.query.Query;
import com.core.sqlTool.config.Constants;
import com.core.sqlTool.model.cache.CacheContext;
import com.core.sqlTool.model.cache.CacheStrategy;
import com.core.sqlTool.model.domain.ExternalHeaderRow;
import com.core.sqlTool.model.resolver.Resolver;
import com.core.sqlTool.utils.DatabaseReader;
import com.core.sqlTool.utils.DtoToModelConverter;
import com.core.sqlTool.utils.printer.CalculatorPrinter;
import com.core.sqlTool.utils.printer.TablePrinter;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {
        var connection = DriverManager.getConnection(String.format("jdbc:%s://localhost:%s/%s", Constants.DB_DRIVER, Constants.DB_PORT, Constants.DB_NAME), Constants.DB_USER, Constants.DB_PASSWORD);
        var projection = new DatabaseReader(connection).read();



        var query = Query.from("courses").as("c")
                .limit(1);



        var modelCommands = new DtoToModelConverter(projection).convert(query.getCommands());
        var cacheContext = new CacheContext(CacheStrategy.NONE);

        var resolverResult = new Resolver(projection, cacheContext).resolve(modelCommands, ExternalHeaderRow.empty());
        var table = resolverResult.lazyTable().fetch();

        var tableStr = new TablePrinter(table).stringify();
        var calculatorStr = new CalculatorPrinter(resolverResult.calculator()).stringify();

        System.out.println(tableStr);
        System.out.println();
        System.out.println(calculatorStr);
    }
}