package com.savchenko.sqlTool.repository;

import com.savchenko.sqlTool.model.Table;
import com.savchenko.sqlTool.model.query.Query;
import com.savchenko.sqlTool.supportive.Constants;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class Projection {
    private final List<Table> tables = new ArrayList<>();
    private final Connection connection = PSQLConnection.get();

    public void addTable(String name) throws SQLException {
        var query = format("select * from %s limit %s", name, Constants.TABLE_MAX_SIZE);
        var resultSet = connection.createStatement().executeQuery(query);
        var table = new Table(name);
        var meta = resultSet.getMetaData();
        var columnsCnt = meta.getColumnCount();

        while (resultSet.next()) {
            var list = new ArrayList<String>(columnsCnt);
            for(int i = 1; i < columnsCnt + 1; i++) {
                list.add(resultSet.getString(i));
            }
            table.addRow(list);
        }
        tables.add(table);
    }

    public Table getByName(String tableName){
        return tables.stream().filter(t -> t.getName().equals(tableName)).findFirst()
                .orElseThrow(() -> new RuntimeException(format("Unable to find table with name '%s'", tableName)));
    }
}
