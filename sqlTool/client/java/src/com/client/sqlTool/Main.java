package com.client.sqlTool;

import com.client.sqlTool.command.Aggregation;
import com.client.sqlTool.command.Order;
import com.client.sqlTool.domain.AggregationType;
import com.client.sqlTool.domain.Column;
import com.client.sqlTool.expression.Binary;
import com.client.sqlTool.query.Query;
import com.core.sqlTool.utils.QueryExecutor;
import com.core.sqlTool.utils.printer.CalculatorPrinter;
import com.core.sqlTool.utils.printer.TablePrinter;

import java.sql.SQLException;

import static com.client.sqlTool.expression.Operator.EQ;

public class Main {

    public static void main(String[] args) {


        var query = Query.from("manufacturer").as("m")
                .innerMergeJoin(Query.from("product").as("p"), Binary.of(EQ, Column.of("p", "bill_id"), Column.of("m", "bill_id")))
                .orderBy(Order.of(Column.of("m_p", "price")).asc(), Order.of(Column.of("m_p", "amount")).asc())
                .groupBy(Column.of("m_p", "ware")).aggregate(
                        Aggregation.of(Column.of("m_p", "amount"), AggregationType.SUM),
                        Aggregation.of(Column.of("m_p", "price"), AggregationType.AVERAGE)
                )

                //.limit(20)
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
            System.out.println(calculatorStr);
        } catch (RuntimeException | SQLException e) {
            System.err.println(e.getMessage());

            e.printStackTrace();
        }
    }

}
