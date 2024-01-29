package com.savchenko.sqlTool.repository;

import com.savchenko.sqlTool.model.expression.Value;
import com.savchenko.sqlTool.model.structure.Column;
import com.savchenko.sqlTool.model.structure.Table;
import com.savchenko.sqlTool.config.Constants;
import com.savchenko.sqlTool.utils.ModelUtils;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import static java.lang.String.format;

public class DBReader {

    public Projection read(DatabaseMetaData metaData) throws SQLException {
        var tables = new LinkedList<Table>();
        var jdbcTables = metaData.getTables(null, Constants.DB_SCHEMA, "%", new String[]{"TABLE"});
        while (jdbcTables.next()) {
            tables.add(addTable(jdbcTables.getString(3)));
        }
        return new Projection(tables);
    }

    private Table addTable(String name) throws SQLException {
        var query = format("select * from %s limit %s", name, Constants.TABLE_PULL_ROWS_MAX_SIZE);
        try (var statement = DBConnection.get().createStatement()) {
            var resultSet = statement.executeQuery(query);
            var meta = resultSet.getMetaData();
            var columnsCnt = meta.getColumnCount();
            var data = new LinkedList<List<Value<?>>>();

            var columns = IntStream.range(1, columnsCnt + 1).mapToObj(i -> {
                try {
                    return new Column(meta.getColumnName(i), name, ModelUtils.getWrapper(Class.forName(meta.getColumnClassName(i))));
                } catch (SQLException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }).toArray(Column[]::new);

            while (resultSet.next()) {
                var list = new ArrayList<Value<?>>(columnsCnt);
                for (int i = 1; i < columnsCnt + 1; i++) {
                    var type = columns[i - 1].type();
                    var entry = ModelUtils.readEntry(resultSet.getString(i), type);
                    list.add(entry);
                }
                data.add(list);
            }
            return new Table(name, Arrays.stream(columns).toList(), data);
        }

    }
}
