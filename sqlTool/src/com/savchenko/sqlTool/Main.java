package com.savchenko.sqlTool;

import com.savchenko.sqlTool.model.query.Query;
import com.savchenko.sqlTool.model.query.QueryResolver;
import com.savchenko.sqlTool.repository.DBReader;
import com.savchenko.sqlTool.repository.PSQLConnection;
import com.savchenko.sqlTool.supportive.Constants;
import com.savchenko.sqlTool.supportive.Printer;

import java.sql.SQLException;

public class Main {
    static {
        PSQLConnection.init(Constants.DB_PORT, Constants.DB_NAME, Constants.DB_USER, Constants.DB_PASSWORD);
    }

    public static void main(String[] args) throws SQLException {
        var connection = PSQLConnection.get();
        var projection = new DBReader().read(connection.getMetaData());
        var resolver = new QueryResolver(projection);
        var printer = new Printer(projection);

        var table = resolver.resolve(
                Query.create(projection)
                        .from("actions")
                        .orderBy("actions.action_id", "actions.solution_id.desc")
                        .limit(60)
        );

        printer.print(table);
    }
}