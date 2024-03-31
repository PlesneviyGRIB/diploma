package com.savchenko.sqlTool.model.domain;

import com.savchenko.sqlTool.model.expression.Value;

import java.util.List;
import java.util.Objects;

public record Table(String name, List<Column> columns, List<List<Value<?>>> data, ExternalRow externalRow) {

    @Override
    public boolean equals(Object o) {
        if (o instanceof Table table) {
            var columnsEquals = columns.size() == table.columns.size();

            for (int i = 0; i < columns.size(); i++) {
                columnsEquals &= columns.get(i).equals(table.columns.get(i));
            }

            if (columnsEquals) {
                var dataEquals = data.size() == table.data.size();

                for (int i = 0; i < data().size(); i++) {
                    var row1 = data.get(i);
                    var row2 = table.data.get(i);

                    dataEquals &= row1.size() == row2.size();

                    for (int k = 0; k < row1.size(); k++) {
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
