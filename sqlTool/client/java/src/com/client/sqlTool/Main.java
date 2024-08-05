package com.client.sqlTool;

import com.client.sqlTool.command.Aggregation;
import com.client.sqlTool.command.Order;
import com.client.sqlTool.domain.Column;
import com.client.sqlTool.query.Query;
import com.core.sqlTool.utils.QueryExecutor;
import com.core.sqlTool.utils.printer.CalculatorPrinter;
import com.core.sqlTool.utils.printer.TablePrinter;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {


        var query = Query.from("product")
                .groupBy(Column.of("ware")).aggregate(Aggregation.sum(Column.of("price")))
                .orderBy(Order.of(Column.of("price")).desc());


        executeAndPrint(query);
    }

    private static void executeAndPrint(Query query) {
        try {
            var result = new QueryExecutor(query).execute();

            var tablePrinter = new TablePrinter(result.getLeft(), 15, 200);
            var calculatorPrinter = new CalculatorPrinter(result.getRight());

            System.out.println(tablePrinter);
            System.out.println();
            System.out.println(calculatorPrinter);
        } catch (RuntimeException | SQLException e) {
            System.err.println(e.getMessage());

            //e.printStackTrace();
        }
    }

}
