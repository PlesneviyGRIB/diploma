package com.savchenko.sqlTool;

import com.savchenko.sqlTool.model.query.Query;
import com.savchenko.sqlTool.model.query.QueryResolver;
import com.savchenko.sqlTool.repository.DBReader;
import com.savchenko.sqlTool.repository.PSQLConnection;
import com.savchenko.sqlTool.repository.Projection;
import com.savchenko.sqlTool.supportive.Constants;
import com.savchenko.sqlTool.supportive.Printer;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

public class Main {
    static {
        PSQLConnection.init(Constants.DB_PORT, Constants.DB_NAME, Constants.DB_USER, Constants.DB_PASSWORD);
    }

    public static void main(String[] args) throws SQLException {
        var connection = PSQLConnection.get();
        var projection = DBReader.read(connection.getMetaData());
        var resolver = new QueryResolver(projection);
        var printer = new Printer(projection);

        var table = resolver.resolve(
                Query.create()
                        .select(List.of())
                        .from(List.of("lms", "sections", "login_password_auth_sources"))
        );

        printer.print(table);
    }
}