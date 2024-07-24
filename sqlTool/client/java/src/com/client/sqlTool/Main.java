package com.client.sqlTool;

import com.client.sqlTool.command.Aggregation;
import com.client.sqlTool.command.Order;
import com.client.sqlTool.domain.AggregationType;
import com.client.sqlTool.domain.Column;
import com.client.sqlTool.expression.Binary;
import com.client.sqlTool.expression.Number;
import com.client.sqlTool.query.Query;
import com.core.sqlTool.utils.QueryExecutor;
import com.core.sqlTool.utils.printer.CalculatorPrinter;
import com.core.sqlTool.utils.printer.TablePrinter;

import java.sql.SQLException;

import static com.client.sqlTool.expression.Operator.EQ;
import static com.client.sqlTool.expression.Operator.PLUS;

public class Main {

    public static void main(String[] args) {


        var query = Query.from("manufacturer").as("m")
                .select(Column.of("bill_id"))
                .limit(5)
                ;


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
