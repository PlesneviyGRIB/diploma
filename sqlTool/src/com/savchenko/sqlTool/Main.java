package com.savchenko.sqlTool;

import com.savchenko.sqlTool.repository.PSQLConnection;
import com.savchenko.sqlTool.repository.Projection;
import com.savchenko.sqlTool.supportive.Constants;

import java.sql.SQLException;

public class Main {
    static {
        PSQLConnection.init(Constants.DB_PORT, Constants.DB_NAME, Constants.DB_USER, Constants.DB_PASSWORD);
    }


    public static void main(String[] args) throws SQLException {
        var connection = PSQLConnection.get();
        var metaData = connection.getMetaData();

        var tables = metaData.getTables(null, Constants.DB_SCHEMA, "%", new String[]{"TABLE"});
        var projection = new Projection();

        while (tables.next()) {
            projection.addTable(tables.getString(3));
        }

        var r = 0;
    }
}