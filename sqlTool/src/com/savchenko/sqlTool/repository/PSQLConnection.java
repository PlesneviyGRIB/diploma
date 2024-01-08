package com.savchenko.sqlTool.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

public class PSQLConnection {
    private static Connection connection;

    private PSQLConnection() {
    }

    public static void init(Integer port, String dbName, String user, String password) {
        try {
            connection = DriverManager.getConnection(String.format("jdbc:postgresql://localhost:%s/%s", port, dbName), user, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection get() {
        return Optional.ofNullable(connection).orElseThrow(() -> new RuntimeException("Init connection first!"));
    }
}
