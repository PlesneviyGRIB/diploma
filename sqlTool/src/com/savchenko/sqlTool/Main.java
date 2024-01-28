package com.savchenko.sqlTool;

import com.savchenko.sqlTool.config.Constants;
import com.savchenko.sqlTool.model.operation.BinaryOperation;
import com.savchenko.sqlTool.model.operation.UnaryOperation;
import com.savchenko.sqlTool.model.operator.LogicOperator;
import com.savchenko.sqlTool.model.structure.Column;
import com.savchenko.sqlTool.query.ColumnRef;
import com.savchenko.sqlTool.query.Query;
import com.savchenko.sqlTool.query.QueryResolver;
import com.savchenko.sqlTool.repository.DBConnection;
import com.savchenko.sqlTool.repository.DBReader;
import com.savchenko.sqlTool.utils.TablePrinter;

import java.sql.SQLException;

public class Main {
    static {
        DBConnection.init(Constants.DB_DRIVER, Constants.DB_PORT, Constants.DB_NAME, Constants.DB_USER, Constants.DB_PASSWORD);
    }

    public static void main(String[] args) throws SQLException {
        var projection = new DBReader().read(DBConnection.get().getMetaData());

        var query = Query.create(projection)
                .selectAll()
                .from("course_users")
                .where(new BinaryOperation(LogicOperator.AND,
                        new UnaryOperation(LogicOperator.IS_NULL, new Column("section_id", "course_users", null)),
                        new UnaryOperation(LogicOperator.EXISTS, new Column("grace_period_starts", "course_users", null))
                ))
                ;

        var resTable = new QueryResolver(projection).resolve(query);
        var resStr = new TablePrinter(resTable).stringify();
        System.out.println(resStr);
    }
}