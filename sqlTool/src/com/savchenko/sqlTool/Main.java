package com.savchenko.sqlTool;

import com.savchenko.sqlTool.config.Constants;
import com.savchenko.sqlTool.model.expression.LongNumber;
import com.savchenko.sqlTool.model.expression.StringValue;
import com.savchenko.sqlTool.model.index.BalancedTreeIndex;
import com.savchenko.sqlTool.query.Q;
import com.savchenko.sqlTool.query.Query;
import com.savchenko.sqlTool.query.QueryResolver;
import com.savchenko.sqlTool.repository.DBConnection;
import com.savchenko.sqlTool.repository.DBReader;
import com.savchenko.sqlTool.utils.TablePrinter;

import java.sql.SQLException;
import java.util.List;

import static com.savchenko.sqlTool.model.operator.Operator.*;

public class Main {
    static {
        DBConnection.init(Constants.DB_DRIVER, Constants.DB_PORT, Constants.DB_NAME, Constants.DB_USER, Constants.DB_PASSWORD);
    }

    public static void main(String[] args) {
        var projection = new DBReader().read(DBConnection.get());


        var query = new Query(projection)
                .from("actions")
                //.constructIndex(new BalancedTreeIndex("", List.of(Q.column("actions", "id")), true))
                .where(
                        Q.op(
                                LESS_OR_EQ,
                                Q.column("actions", "id"),
                                Q.op(
                                        MULTIPLY,
                                        new LongNumber(1L),
                                        new LongNumber(1042L)
                                )
                        ),
                        Q.op(EQ, Q.column("actions", "action_id"), new StringValue("addRow"))
                )
                .orderBy(Q.order(Q.column("actions", "solution_id")), Q.order(Q.column("actions", "id"), true))
                //.limit(20)
                ;


        var resTable = new QueryResolver().resolve(query);

        var resStr = new TablePrinter(resTable).stringify();
        System.out.println(resStr);
    }
}