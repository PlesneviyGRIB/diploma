package com.savchenko.sqlTool;

import com.savchenko.sqlTool.config.Constants;
import com.savchenko.sqlTool.query.Q;
import com.savchenko.sqlTool.query.Query;
import com.savchenko.sqlTool.query.QueryResolver;
import com.savchenko.sqlTool.repository.DBConnection;
import com.savchenko.sqlTool.repository.DBReader;
import com.savchenko.sqlTool.utils.TablePrinter;

import java.sql.SQLException;

import static com.savchenko.sqlTool.model.operator.Operator.*;

public class Main {
    static {
        DBConnection.init(Constants.DB_DRIVER, Constants.DB_PORT, Constants.DB_NAME, Constants.DB_USER, Constants.DB_PASSWORD);
    }

    public static void main(String[] args) throws SQLException {
        var projection = new DBReader().read(DBConnection.get().getMetaData());




        var query = Query.create()
                .from("actions")
                .selectAll()
                //.select(Q.column("actions", "id"))
//                .where(Q.op(AND,
//                        Q.op(EXISTS, Q.column("course_users", "id")),
//                        Q.op(IS_NULL, Q.column("course_users", "user_id"))
//                ))
                //.orderBy(Q.order(Q.column("actions", "id")))
                .orderBy(Q.order(Q.column("actions", "action_label")))
                .limit(10)
                ;





        var resTable = new QueryResolver(projection).resolve(query);
        var resStr = new TablePrinter(resTable).stringify();
        System.out.println(resStr);
    }
}