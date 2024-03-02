package com.savchenko.sqlTool.utils;

import com.savchenko.sqlTool.model.expression.StringValue;
import com.savchenko.sqlTool.model.expression.Value;
import com.savchenko.sqlTool.model.domain.Column;
import com.savchenko.sqlTool.model.domain.Table;

import java.util.ArrayList;
import java.util.stream.IntStream;

import static java.lang.String.format;

public class TablePrinter {
    private static final Integer COLUMN_WIDTH = 12;
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
        delimiterRow();
        appendColumnNames();
        appendColumnTypes();
        delimiterRow();
        appendRows();
        checkForOverLimit();
        delimiterRow();

        return res.toString();
    }

    private void appendMetadata() {
        res.append(format("Table '%s' [%d rows]\n", table.name().toUpperCase(), rowsCount()));
    }

    private void appendColumnNames() {
        res.append("|");
        table.columns().stream()
                .map(Column::name)
                .forEach(name -> res.append(formatCell(new StringValue(name))).append("|"));
        res.append("\n");
    }

    private void appendColumnTypes() {
        res.append("|");
        table.columns().stream()
                .map(Column::type)
                .forEach(type -> {
                    var typeName = type.getSimpleName()
                            .replace("Value", "")
                            .replace("Number", "")
                            .toUpperCase();
                    res.append(formatCell(new StringValue(typeName))).append("|");
                });
        res.append("\n");
    }

    private void appendRows() {
        var array = new ArrayList<>(table.data());
        for (int i = 0; i < Math.min(rowsCount(), PRINTED_ROWS_MAX_COUNT); i++) {
            var row = array.get(i);
            res.append("|");
            row.forEach(val -> res.append(formatCell(val)).append("|"));
            res.append("\n");
        }
    }

    private void checkForOverLimit() {
        if (PRINTED_ROWS_MAX_COUNT < rowsCount()) {
            res.append("|");
            IntStream.range(0, columnsCount())
                    .forEach(i -> res.append(formatCell(new StringValue("..."))).append("|"));
            res.append("\n");
        }
    }

    private String formatCell(Value<?> entry) {
        var dataWidth = COLUMN_WIDTH - 2;
        var format = " %" + dataWidth + "." + dataWidth + "s ";
        return format(format, entry.stringify());
    }

    private void delimiterRow() {
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
