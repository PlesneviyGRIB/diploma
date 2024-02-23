package com.savchenko.sqlTool.model.structure;

import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.expression.Value;
import com.savchenko.sqlTool.model.index.Index;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.String.format;

public record Table(String name, List<Column> columns, List<List<Value<?>>> data, List<Index> indices) implements Expression<Table> {
    public boolean isEmpty() {
        return this.data.isEmpty();
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int compareTo(Table table) {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Table table) {
            var columnsEquals = columns.size() == table.columns.size() &&
                    IntStream.range(0, columns.size()).allMatch(index -> columns.get(index).equals(table.columns.get(index)));
            if(columnsEquals) {
                return data.size() == table.data.size() &&
                        IntStream.range(0, data.size())
                                .allMatch(rowIndex -> {
                                    var row1 = data.get(rowIndex);
                                    var row2 = table.data.get(rowIndex);
                                    return row1.size() == row2.size() &&
                                            IntStream.range(0, row1.size()).allMatch(index -> row1.get(index).equals(row2.get(index)));
                                });
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, columns, data);
    }
}
