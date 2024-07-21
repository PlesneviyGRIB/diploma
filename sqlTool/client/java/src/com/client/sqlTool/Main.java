package com.client.sqlTool;

import com.client.sqlTool.domain.Column;
import com.client.sqlTool.expression.Binary;
import com.client.sqlTool.expression.Number;
import com.client.sqlTool.query.Query;
import com.core.sqlTool.utils.QueryExecutor;
import com.core.sqlTool.utils.printer.TablePrinter;

import java.sql.SQLException;

import static com.client.sqlTool.expression.Operator.*;

public class Main {

    public static void main(String[] args) {


        var query = Query.from("courses")
                .select(Column.of("courses", "id"), Binary.of(PLUS, Column.of("courses", "id"), Column.of("courses", "id")))
                .where(Binary.of(GREATER_OR_EQ, Column.of("courses", "id"), Number.of(5)))
                .limit(20);


        executeAndPrint(query);
    }

    private static void executeAndPrint(Query query) {
        try {
            var result = new QueryExecutor(query).execute();

            var tableStr = new TablePrinter(result.getLeft()).stringify();
            //var calculatorStr = new CalculatorPrinter(result.getRight()).stringify();

            System.out.println(tableStr);
            System.out.println();
            //System.out.println(calculatorStr);
        } catch (RuntimeException | SQLException e) {
            System.err.println(e.getMessage());

            e.printStackTrace();
        }
    }

}
