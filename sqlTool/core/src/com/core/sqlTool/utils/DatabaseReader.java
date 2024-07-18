package com.core.sqlTool.utils;

import com.core.sqlTool.config.Constants;
import com.core.sqlTool.model.domain.Column;
import com.core.sqlTool.model.domain.Projection;
import com.core.sqlTool.model.domain.Row;
import com.core.sqlTool.model.domain.Table;
import com.core.sqlTool.model.expression.Value;
import com.core.sqlTool.model.index.BalancedTreeIndex;
import com.core.sqlTool.model.index.HashIndex;
import com.core.sqlTool.model.index.Index;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class DatabaseReader {

    private final Connection connection;

    public DatabaseReader(Connection connection) {
        this.connection = connection;
    }

    public Projection read() throws SQLException {
        var metaData = connection.getMetaData();
        var jdbcTables = metaData.getTables(Constants.DB_NAME, Constants.DB_SCHEMA, "%", new String[]{"TABLE"});

        var tables = new LinkedList<Table>();

        while (jdbcTables.next()) {
            tables.add(addTable(jdbcTables.getString(3)));
        }

        return new Projection(tables);
    }

    private Table addTable(String tableName) throws SQLException {
        try (var statement = connection.createStatement()) {

            var query = format("select * from %s limit %s", tableName, Constants.TABLE_PULL_ROWS_MAX_SIZE);
            var resultSet = statement.executeQuery(query);

            var metaData = resultSet.getMetaData();
            var columnsCount = metaData.getColumnCount();

            var columns = readColumns(tableName, metaData);
            var indices = readIndices(tableName, columns);
            var data = new LinkedList<Row>();

            while (resultSet.next()) {
                var row = new LinkedList<Value<?>>();

                for (int i = 1; i < columnsCount + 1; i++) {
                    var type = columns.get(i - 1).columnType();
                    var entry = ModelUtils.readEntry(resultSet.getString(i), type);
                    row.add(entry);
                }

                data.add(new Row(row));
            }

            return new Table(tableName, columns, data);
        }
    }

    private List<Column> readColumns(String tableName, ResultSetMetaData metaData) throws SQLException {
        var columnsCount = metaData.getColumnCount();
        var columns = new ArrayList<Column>(columnsCount);

        for (int i = 1; i <= columnsCount; i++) {
            var columnName = metaData.getColumnName(i);
            try {
                var columnClassType = ModelUtils.getWrapper(Class.forName(metaData.getColumnClassName(i)));
                var column = new Column(columnName, tableName, columnClassType);
                columns.add(column);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Fatal while reading database");
            }
        }

        return columns;
    }

    private List<Index> readIndices(String tableName, List<Column> columns) throws SQLException {
        var metaData = connection.getMetaData();
        var resultSet = metaData.getIndexInfo(Constants.DB_NAME, Constants.DB_SCHEMA, tableName, false, false);
        var rawData = new LinkedList<List<String>>();

        while (resultSet.next()) {

            var indexName = resultSet.getString("INDEX_NAME");
            var columnName = resultSet.getString("COLUMN_NAME");
            var order = resultSet.getString("ORDINAL_POSITION");
            var type = resultSet.getString("TYPE");
            var isForUniqueValues = resultSet.getString("NON_UNIQUE");

            rawData.add(List.of(indexName, columnName, order, type, isForUniqueValues));
        }

        return rawData.stream()
                .filter(row -> row.get(3).equals("2") || row.get(3).equals("3"))
                .collect(Collectors.toMap(r -> r.get(0), List::of, ListUtils::union))
                .entrySet().stream()
                .map(entry -> Pair.of(entry.getKey(), entry.getValue().stream().sorted((r1, r2) -> r1.get(3).compareTo(r2.get(3))).toList()))
                .map(pair -> {
                    var indexName = pair.getLeft();
                    var data = pair.getRight();
                    var rawIndexData = data.get(0);

                    var targetColumns = data.stream()
                            .map(r -> r.get(1))
                            .map(columnName -> columns.stream().filter(c -> c.columnName().equals(columnName)).findFirst().get())
                            .toList();

                    if (rawIndexData.get(3).equals("2")) {
                        return new HashIndex(indexName, targetColumns);
                    } else {
                        return new BalancedTreeIndex(indexName, targetColumns);
                    }
                }).toList();
    }
}
