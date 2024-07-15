package com.core.sqlTool.model.command;

import com.core.sqlTool.exception.UnexpectedException;
import com.core.sqlTool.model.command.domain.SimpleCalculedCommand;
import com.core.sqlTool.model.command.function.AggregationFunction;
import com.core.sqlTool.model.command.function.Identity;
import com.core.sqlTool.model.complexity.CalculatorEntry;
import com.core.sqlTool.model.domain.*;
import com.core.sqlTool.model.expression.ExpressionList;
import com.core.sqlTool.model.expression.Value;
import com.core.sqlTool.utils.ModelUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.stream.Collectors.groupingBy;

public class GroupByCommand implements SimpleCalculedCommand {

    private final Map<Column, AggregationFunction> columnMapperMap;

    public GroupByCommand(Map<Column, AggregationFunction> columnMapperMap) {
        this.columnMapperMap = columnMapperMap;
    }

    @Override
    public LazyTable run(LazyTable lazyTable, Projection projection, CalculatorEntry calculatorEntry) {

        var table = lazyTable.fetch();

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

        if (groupColumns.isEmpty()) {
            var pair = collapseTableToOneRow(table);
            IntStream.range(0, pair.getRight()).forEach(calculatorEntry::count);
            var targetTable = pair.getLeft();
            return new LazyTable(targetTable.name(), targetTable.columns(), targetTable.data().stream(), lazyTable.externalRow());
        }

        var wrappedValues = table.data().stream()
                .map(row -> row.values().stream().map(List::<Value<?>>of).toList())
                .toList();

        var functions = table.columns().stream().map(columnMapperMap::get).toList();
        var indexes = groupColumns.stream().map(c -> ModelUtils.resolveColumnIndex(table.columns(), c)).toList();

        for (var index : indexes) {
            wrappedValues = processGroups(index, wrappedValues, functions);
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


        List<Row> data = wrappedValues.stream()
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
                })
                .map(Row::new)
                .toList();

        var complexity = table.columns().size() * table.data().size() * groupColumns.size();

        IntStream.range(0, complexity).forEach(calculatorEntry::count);

        return new LazyTable(table.name(), table.columns(), data.stream(), lazyTable.externalRow());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupByCommand groupBy = (GroupByCommand) o;
        return Objects.equals(columnMapperMap, groupBy.columnMapperMap);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(columnMapperMap);
    }

    private Pair<Table, Integer> collapseTableToOneRow(Table table) {
        var columns = table.columns();
        var targetColumns = new ArrayList<Column>();
        var values = new ArrayList<Value<?>>();
        var complexity = columns.size() * table.data().size();

        Function<Integer, ExpressionList> getColumnValues = index -> {
            var list = table.data().stream()
                    .map(row -> row.values().get(index))
                    .toList();
            return new ExpressionList(list, table.columns().get(index).type());
        };

        for (int i = 0; i < columns.size(); i++) {
            var column = columns.get(i);
            var function = columnMapperMap.get(column);
            var columnName = format("%s.%s", column.name(), function.getClass().getSimpleName().toUpperCase());
            targetColumns.add(new Column(columnName, column.table(), column.type()));
            values.add(function.apply(getColumnValues.apply(i)));
        }

        return Pair.of(new Table(table.name(), targetColumns, Stream.of(values).map(Row::new).toList()), complexity);
    }

    private List<List<List<Value<?>>>> processGroups(Integer index, List<List<List<Value<?>>>> groupedData, List<AggregationFunction> functions) {
        return groupedData.stream()
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
    }
}
