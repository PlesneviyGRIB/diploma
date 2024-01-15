package com.savchenko.sqlTool.repository;

import com.savchenko.sqlTool.model.Column;
import com.savchenko.sqlTool.model.Table;
import com.savchenko.sqlTool.model.query.Query;
import com.savchenko.sqlTool.supportive.Constants;
import org.postgresql.jdbc.PgResultSetMetaData;

import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import static java.lang.String.format;

public class Projection {
    private final List<Table> tables = new ArrayList<>();
    private final Connection connection = PSQLConnection.get();

    public void addTable(String name) throws SQLException {
        var query = format("select * from %s limit %s", name, Constants.TABLE_MAX_SIZE);
        var resultSet = connection.createStatement().executeQuery(query);
        ResultSetMetaData meta = resultSet.getMetaData();
        var columnsCnt = meta.getColumnCount();
        List<List<String>> data = new LinkedList<>();
        var columns = IntStream.range(1, columnsCnt + 1).mapToObj(i -> {
            try {
                return new Column(meta.getColumnName(i), name);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).toList();

        while (resultSet.next()) {
            var list = new ArrayList<String>(columnsCnt);
            for(int i = 1; i < columnsCnt + 1; i++) {
                list.add(resultSet.getString(i));
            }
            data.add(list);
        }
        tables.add(new Table(name, columns, data));
    }

    public Table getByName(String tableName){
        return tables.stream().filter(t -> t.name().equals(tableName)).findFirst()
                .orElseThrow(() -> new RuntimeException(format("Unable to find table with name '%s'", tableName)));
    }
}
