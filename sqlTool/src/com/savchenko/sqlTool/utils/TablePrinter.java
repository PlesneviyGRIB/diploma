package com.savchenko.sqlTool.utils;

import com.savchenko.sqlTool.model.expression.Value;
import com.savchenko.sqlTool.model.structure.Column;
import com.savchenko.sqlTool.model.structure.Table;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.lang.String.format;

public class TablePrinter {
    private static final Integer COLUMN_WIDTH = 15;
    private static final Integer PRINTED_ROWS_MAX_COUNT = 200;
    private final Table table;
    private final StringBuilder res;

    public TablePrinter(Table table) {
        this.table = table;
        this.res = new StringBuilder();
    }

    public String stringify() {
        if (!res.isEmpty()) {
            return res.toString();
        }

        appendMetadata();
        emptyRow();
        appendColumnNames();
        emptyRow();
        appendRows();
        checkForOverLimit();
        emptyRow();

        return res.toString();
    }

    private void appendMetadata() {
        res.append(format("Table '%s' [%d rows]\n", table.name().toUpperCase(), rowsCount()));
    }

    private void appendColumnNames() {
        res.append("|");
        table.columns().stream()
                .map(Column::name)
                .forEach(name -> res.append(formatCell(name)).append("|"));
        res.append("\n");
    }

    private void appendRows() {
        var array = new ArrayList<>(table.data());
        for (int i = 0; i < Math.min(rowsCount(), PRINTED_ROWS_MAX_COUNT); i++) {
            var row = array.get(i);
            res.append("|");
            for (Value entry : row) {
                res.append(formatCell(entry)).append("|");
            }
            res.append("\n");
        }
    }

    private void checkForOverLimit() {
        if (PRINTED_ROWS_MAX_COUNT < rowsCount()) {
            res.append("|");
            IntStream.range(0, columnsCount())
                    .forEach(i -> res.append(formatCell("...")).append("|"));
            res.append("\n");
        }
    }

    private String formatCell(Object entry) {
        return format("%" + COLUMN_WIDTH + "." + COLUMN_WIDTH + "s", entry);
    }

    private void emptyRow() {
        res.append("+");
        for (int i = 0; i < columnsCount(); i++) {
            for (int j = 0; j < COLUMN_WIDTH; j++) {
                res.append("-");
            }
            res.append("+");
        }
        res.append("\n");
    }

    private Integer rowsCount() {
        return table.data().size();
    }

    private Integer columnsCount() {
        return table.columns().size();
    }
}
