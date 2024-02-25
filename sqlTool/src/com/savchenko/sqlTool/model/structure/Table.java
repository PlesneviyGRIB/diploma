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
            var columnsEquals = columns.size() == table.columns.size();

            for(int i = 0; i < columns.size(); i++) {
                columnsEquals &= columns.get(i).equals(table.columns.get(i));
            }

            if(columnsEquals) {
                var dataEquals = data.size() == table.data.size();

                for(int i = 0; i < data().size(); i++) {
                    var row1 = data.get(i);
                    var row2 = table.data.get(i);

                    dataEquals &= row1.size() == row2.size();

                    for(int k = 0; k < row1.size(); k++) {
                        dataEquals &= row1.get(k).equals(row2.get(k));
                    }
                }

                return dataEquals;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, columns, data);
    }
}
