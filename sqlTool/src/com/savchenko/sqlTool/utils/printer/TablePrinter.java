package com.savchenko.sqlTool.utils.printer;

import com.savchenko.sqlTool.model.domain.Column;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.model.expression.StringValue;
import com.savchenko.sqlTool.model.expression.Value;

import java.util.ArrayList;
import java.util.stream.IntStream;

import static java.lang.String.format;

public class TablePrinter extends Printer<Table> {
    private static final Integer COLUMN_WIDTH = 12;
    private static final Integer PRINTED_ROWS_MAX_COUNT = 200;

    public TablePrinter(Table table) {
        super(table);
    }

    @Override
    protected void buildString() {
        appendMetadata();
        delimiterRow();
        appendColumnNames();
        appendColumnTypes();
        delimiterRow();
        appendRows();
        checkForOverLimit();
        delimiterRow();
    }

    private void appendMetadata() {
        sb.append(format("Table '%s' [%d rows]\n", domain.name().toUpperCase(), rowsCount()));
    }

    private void appendColumnNames() {
        sb.append("|");
        domain.columns().stream()
                .map(Column::name)
                .forEach(name -> sb.append(formatCell(new StringValue(name))).append("|"));
        sb.append("\n");
    }

    private void appendColumnTypes() {
        sb.append("|");
        domain.columns().stream()
                .map(Column::type)
                .forEach(type -> {
                    var typeName = type.getSimpleName()
                            .replace("Value", "")
                            .replace("Number", "")
                            .toUpperCase();
                    sb.append(formatCell(new StringValue(typeName))).append("|");
                });
        sb.append("\n");
    }

    private void appendRows() {
        var array = domain.dataStream().toList();
        for (int i = 0; i < Math.min(rowsCount(), PRINTED_ROWS_MAX_COUNT); i++) {
            var row = array.get(i);
            sb.append("|");
            row.forEach(val -> sb.append(formatCell(val)).append("|"));
            sb.append("\n");
        }
    }

    private void checkForOverLimit() {
        if (PRINTED_ROWS_MAX_COUNT < rowsCount()) {
            sb.append("|");
            IntStream.range(0, columnsCount())
                    .forEach(i -> sb.append(formatCell(new StringValue("..."))).append("|"));
            sb.append("\n");
        }
    }

    private String formatCell(Value<?> entry) {
        var dataWidth = COLUMN_WIDTH - 2;
        var format = " %" + dataWidth + "." + dataWidth + "s ";
        return format(format, entry.stringify());
    }

    private void delimiterRow() {
        sb.append("+");
        for (int i = 0; i < columnsCount(); i++) {
            sb.append("-".repeat(COLUMN_WIDTH));
            sb.append("+");
        }
        sb.append("\n");
    }

    private Integer rowsCount() {
        return domain.dataStream().toList().size();
    }

    private Integer columnsCount() {
        return domain.columns().size();
    }
}
