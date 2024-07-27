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


        var query = Query.from("product").as("p")
                .select(Column.of("ware"), Column.of("price"))
                .groupBy(Column.of("ware")).aggregate(
                        Aggregation.sum(Column.of("price")),
                        Aggregation.count(Column.of("p.price").as("count")),
                        Aggregation.average(Column.of("p.price").as("tmp"))
                )
                .orderBy(Order.of(Column.of("price")).desc())
                .limit(20);


        executeAndPrint(query);
    }

    private static void executeAndPrint(Query query) {
        try {
            var result = new QueryExecutor(query).execute();

            var tableStr = new TablePrinter(result.getLeft()).stringify();
            var calculatorStr = new CalculatorPrinter(result.getRight()).stringify();

            System.out.println(tableStr);
            System.out.println();
            //System.out.println(calculatorStr);
        } catch (RuntimeException | SQLException e) {
            System.err.println(e.getMessage());

            e.printStackTrace();
        }
    }

}
