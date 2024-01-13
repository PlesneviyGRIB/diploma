package com.savchenko.sqlTool.repository;

import com.savchenko.sqlTool.supportive.Constants;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class DBReader {
    public static Projection read(DatabaseMetaData metaData) throws SQLException {
        var projection = new Projection();
        var tables = metaData.getTables(null, Constants.DB_SCHEMA, "%", new String[]{"TABLE"});
        while (tables.next()) {
            projection.addTable(tables.getString(3));
        }
        return projection;
    }
}
