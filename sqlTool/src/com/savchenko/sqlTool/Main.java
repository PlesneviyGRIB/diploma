package com.savchenko.sqlTool;

import com.savchenko.sqlTool.model.query.Query;
import com.savchenko.sqlTool.model.query.QueryResolver;
import com.savchenko.sqlTool.repository.DBReader;
import com.savchenko.sqlTool.repository.PSQLConnection;
import com.savchenko.sqlTool.supportive.Constants;
import com.savchenko.sqlTool.supportive.TablePrinter;

import java.sql.SQLException;

public class Main {
    static {
        PSQLConnection.init(Constants.DB_PORT, Constants.DB_NAME, Constants.DB_USER, Constants.DB_PASSWORD);
    }

    public static void main(String[] args) throws SQLException {
        var connection = PSQLConnection.get();
        var projection = new DBReader().read(connection.getMetaData());
        var resolver = new QueryResolver(projection);

        var table = resolver.resolve(
                Query.create(projection)
                        .select("*")
                        .from("actions", "comment", "tag")
                        .orderBy("tag.id")
                        .limit(700)
        );

        System.out.println(new TablePrinter(table).stringify());
    }
}