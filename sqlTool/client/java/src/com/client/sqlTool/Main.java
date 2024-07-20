package com.client.sqlTool;

import com.client.sqlTool.domain.Column;
import com.client.sqlTool.query.Query;
import com.core.sqlTool.utils.QueryExecutor;
import com.core.sqlTool.utils.printer.TablePrinter;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {


        var query = Query.from("courses")
                .select(Column.of("courses", "id"))
                .limit(20);

        executeAndPrint(query);
    }

    private static void executeAndPrint(Query query) throws SQLException {
        var result = new QueryExecutor(query).execute();

        var tableStr = new TablePrinter(result.getLeft()).stringify();
        //var calculatorStr = new CalculatorPrinter(result.getRight()).stringify();

        System.out.println(tableStr);
        System.out.println();
        //System.out.println(calculatorStr);
    }

}
