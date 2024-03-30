package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.exception.UnexpectedException;
import com.savchenko.sqlTool.model.command.domain.SimpleCalculedCommand;
import com.savchenko.sqlTool.model.command.function.AggregationFunction;
import com.savchenko.sqlTool.model.command.function.Identity;
import com.savchenko.sqlTool.model.complexity.Calculator;
import com.savchenko.sqlTool.model.domain.Column;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.model.expression.ExpressionList;
import com.savchenko.sqlTool.model.expression.Value;
import com.savchenko.sqlTool.utils.ModelUtils;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.lang.String.format;
import static java.util.stream.Collectors.groupingBy;

public class GroupBy extends SimpleCalculedCommand {

    private final Map<Column, AggregationFunction> columnMapperMap;

    public GroupBy(Map<Column, AggregationFunction> columnMapperMap, Projection projection) {
        super(projection);
        this.columnMapperMap = columnMapperMap;
    }

    @Override
    public Table run(Table table, Calculator calculator) {

        // TODO rewrite

        table.columns().stream()
                .filter(c -> !columnMapperMap.containsKey(c))
                .findAny()
                .ifPresent(c -> {
                    throw new UnexpectedException("Column '%s' does not presents in group by statement", c);
                });

        var groupColumns = columnMapperMap.entrySet().stream()
                .filter(entry -> entry.getValue() instanceof Identity)
                .map(Map.Entry::getKey)
                .toList();

        Function<Integer, ExpressionList> getColumnValues = index -> {
            var list = table.data().stream()
                    .map(row -> row.get(index))
                    .toList();

            return new ExpressionList(list, table.columns().get(index).type());
        };

        if (groupColumns.isEmpty()) {
            var columns = table.columns();
            var targetColumns = new ArrayList<Column>();
            var values = new ArrayList<Value<?>>();

            for (int i = 0; i < columns.size(); i++) {
                var column = columns.get(i);
                var function = columnMapperMap.get(column);
                var columnName = format("%s.%s", column.name(), function.getClass().getSimpleName().toUpperCase());
                targetColumns.add(new Column(columnName, column.table(), column.type()));
                values.add(function.apply(getColumnValues.apply(i)));
            }

            return new Table(table.name(), targetColumns, List.of(values), List.of());
        }


        List<AggregationFunction> functions = table.columns().stream().map(columnMapperMap::get).toList();

        BiFunction<Integer, List<List<List<Value<?>>>>, List<List<List<Value<?>>>>> processGroups = (index, groupedData) -> groupedData.stream()
                .map(group -> {
                    BiFunction<Integer, List<List<List<Value<?>>>>, List<List<List<Value<?>>>>> tmp = (index1, rows) ->
                            rows.stream().map(r -> {
                                List<List<List<Value<?>>>> targetRows = new LinkedList<>();
                                if (functions.get(index1) instanceof Identity) {
                                    var list = group.get(index1);
                                    list.forEach(value -> {
                                        List<List<Value<?>>> phantomRow = new ArrayList<>(group);
                                        phantomRow.set(index1, List.of(value));
                                        targetRows.add(phantomRow);
                                    });
                                }
                                return targetRows.isEmpty() ? List.of(r) : targetRows;
                            }).flatMap(Collection::stream).toList();

                    List<List<List<Value<?>>>> targetGroup = new LinkedList<>();
                    for (int i = 0; i < group.size(); i++) {
                        targetGroup = tmp.apply(i, List.of(group));
                    }

                    return targetGroup.isEmpty() ? List.of(group) : targetGroup;
                }).flatMap(Collection::stream)
                .collect(groupingBy(row -> row.get(index).get(0)))
                .entrySet().stream()
                .map(entry -> {
                    var rows = entry.getValue();
                    var size = rows.get(0).size();
                    List<List<Value<?>>> groupedRow = new ArrayList<>(size);

                    for (int i = 0; i < size; i++) {
                        groupedRow.add(new LinkedList<>());
                        for (int j = 0; j < rows.size(); j++) {
                            groupedRow.get(i).addAll(rows.get(j).get(i));
                        }
                    }

                    groupedRow.set(index, List.of(entry.getKey()));
                    return groupedRow;
                }).toList();

        var wrappedValues = table.data().stream()
                .map(row -> row.stream().map(List::<Value<?>>of).toList())
                .toList();

        var indexes = groupColumns.stream().map(c -> ModelUtils.resolveColumnIndex(table.columns(), c)).toList();

        for (var index : indexes) {
            wrappedValues = processGroups.apply(index, wrappedValues);
        }

        wrappedValues = wrappedValues.stream()
                .map(group -> {
                    BiFunction<Integer, List<List<List<Value<?>>>>, List<List<List<Value<?>>>>> tmp = (index, rows) ->
                            rows.stream().map(r -> {
                                List<List<List<Value<?>>>> targetRows = new LinkedList<>();
                                if (functions.get(index) instanceof Identity) {
                                    var list = group.get(index);
                                    list.forEach(value -> {
                                        List<List<Value<?>>> phantomRow = new ArrayList<>(group);
                                        phantomRow.set(index, List.of(value));
                                        targetRows.add(phantomRow);
                                    });
                                }
                                return targetRows.isEmpty() ? List.of(r) : targetRows;
                            }).flatMap(Collection::stream).toList();

                    List<List<List<Value<?>>>> targetGroup = new LinkedList<>();
                    for (int i = 0; i < group.size(); i++) {
                        targetGroup = tmp.apply(i, List.of(group));
                    }

                    return targetGroup.isEmpty() ? List.of(group) : targetGroup;
                }).flatMap(Collection::stream).toList();


        List<List<Value<?>>> data = wrappedValues.stream()
                .map(groupedRow -> {
                    var size = groupedRow.size();
                    List<Value<?>> row = new ArrayList<>(size);

                    for (int i = 0; i < size; i++) {

                        var list = groupedRow.get(i);
                        var column = table.columns().get(i);
                        var expressionList = new ExpressionList(list, column.type());
                        var func = functions.get(i);

                        Value<?> value = func.apply(expressionList);
                        row.add(i, value);
                    }
                    return row;
                }).toList();

        return new Table(table.name(), table.columns(), data, List.of());
    }

}
