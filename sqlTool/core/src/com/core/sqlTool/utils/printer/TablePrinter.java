package com.core.sqlTool.utils.printer;

import com.core.sqlTool.model.domain.Column;
import com.core.sqlTool.model.domain.Table;
import com.core.sqlTool.model.expression.StringValue;
import com.core.sqlTool.model.expression.Value;

import java.util.ArrayList;
import java.util.stream.IntStream;

import static java.lang.String.format;

public class TablePrinter extends Printer<Table> {
    private static final Integer COLUMN_WIDTH = 10;
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
                .map(Column::getColumnName)
                .forEach(name -> sb.append(formatCell(new StringValue(name))).append("|"));
        sb.append("\n");
    }

    private void appendColumnTypes() {
        sb.append("|");
        domain.columns().stream()
                .map(Column::getColumnType)
                .forEach(type -> {
                    var typeName = type.getSimpleName()
                            .replace("Value", "")
                            .toUpperCase();
                    sb.append(formatCell(new StringValue(typeName))).append("|");
                });
        sb.append("\n");
    }

    private void appendRows() {
        var array = new ArrayList<>(domain.data());
        for (int i = 0; i < Math.min(rowsCount(), PRINTED_ROWS_MAX_COUNT); i++) {
            var row = array.get(i);
            sb.append("|");
            row.values().forEach(val -> sb.append(formatCell(val)).append("|"));
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
        return domain.data().size();
    }

    private Integer columnsCount() {
        return domain.columns().size();
    }
}
