package com.savchenko.sqlTool.repository;

import com.savchenko.sqlTool.model.expression.Value;
import com.savchenko.sqlTool.model.index.BalancedTreeIndex;
import com.savchenko.sqlTool.model.index.HashIndex;
import com.savchenko.sqlTool.model.index.Index;
import com.savchenko.sqlTool.model.structure.Column;
import com.savchenko.sqlTool.model.structure.Table;
import com.savchenko.sqlTool.config.Constants;
import com.savchenko.sqlTool.utils.ModelUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.String.format;

public class DBReader {

    public Projection read(Connection connection) {
        try {
            var metaData = connection.getMetaData();
            var jdbcTables = metaData.getTables(Constants.DB_NAME, Constants.DB_SCHEMA, "%", new String[]{"TABLE"});

            var tables = new LinkedList<Table>();
            while (jdbcTables.next()) {
                tables.add(addTable(jdbcTables.getString(3), metaData));
            }
            return new Projection(tables);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("An error occurs during reading tables");
        }
    }

    private Table addTable(String tableName, DatabaseMetaData databaseMetaData) throws SQLException, ClassNotFoundException {
        try (var statement = DBConnection.get().createStatement()) {

            var query = format("select * from %s limit %s", tableName, Constants.TABLE_PULL_ROWS_MAX_SIZE);
            var resultSet = statement.executeQuery(query);

            var metaData = resultSet.getMetaData();
            var columnsCount = metaData.getColumnCount();

            var columns = readColumns(tableName, metaData);
            var indices = readIndices(tableName, columns, databaseMetaData);
            var data = new LinkedList<List<Value<?>>>();

            while (resultSet.next()) {
                var list = new ArrayList<Value<?>>(columnsCount);

                for (int i = 1; i < columnsCount + 1; i++) {
                    var type = columns.get(i - 1).type();
                    var entry = ModelUtils.readEntry(resultSet.getString(i), type);
                    list.add(entry);
                }

                data.add(list);
            }

            return new Table(tableName, columns, data, indices);
        }
    }

    private List<Column> readColumns(String tableName, ResultSetMetaData metaData) throws SQLException, ClassNotFoundException {
        var columnsCount = metaData.getColumnCount();
        var columns = new ArrayList<Column>(columnsCount);

        for (int i = 1; i <= columnsCount; i++) {
            var columnName = metaData.getColumnName(i);
            var columnClassType = ModelUtils.getWrapper(Class.forName(metaData.getColumnClassName(i)));
            var ocolumn = new Column(columnName, tableName, columnClassType);
            columns.add(ocolumn);
        }

        return columns;
    }

    private List<Index> readIndices(String tableName, List<Column> columns, DatabaseMetaData metaData) throws SQLException {
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
                    var forUniqueValues = rawIndexData.get(2).equals("f");

                    var targetColumns = data.stream()
                            .map(r -> r.get(1))
                            .map(columnName -> columns.stream().filter(c -> c.name().equals(columnName)).findFirst().get())
                            .toList();

                    if(rawIndexData.get(3).equals("2")) {
                        return new HashIndex(indexName, targetColumns, forUniqueValues);
                    } else {
                        return new BalancedTreeIndex(indexName, targetColumns, forUniqueValues);
                    }
                }).toList();
    }
}
