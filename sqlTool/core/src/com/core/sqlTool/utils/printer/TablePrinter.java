package com.core.sqlTool.utils.printer;

import com.core.sqlTool.model.domain.Column;
import com.core.sqlTool.model.domain.Table;
import com.core.sqlTool.model.expression.StringValue;
import com.core.sqlTool.model.expression.Value;
import com.core.sqlTool.utils.PrinterUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.IntStream;

import static java.lang.String.format;

@RequiredArgsConstructor
public class TablePrinter {

    private final Table table;

    private final Integer columnWidth;

    private final Integer rowsMaxCount;

    private final StringBuilder stringBuilder = new StringBuilder();

    @Override
    public String toString() {
        stringBuilder.setLength(0);

        appendMetadata();
        delimiterRow();
        appendColumnNames();
        appendColumnTypes();
        delimiterRow();
        appendRows();
        checkForOverLimit();
        delimiterRow();

        return stringBuilder.toString();
    }

    private void appendMetadata() {
        stringBuilder.append(format("Table '%s' [%d rows]\n", StringUtils.defaultIfEmpty(table.name(), "RESULT"), rowsCount()));
    }

    private void appendColumnNames() {

        stringBuilder.append("|").append(formatNumber(null));
        stringBuilder.append("|");

        table.columns().stream()
                .map(Column::getColumnName)
                .forEach(name -> stringBuilder.append(formatCell(new StringValue(name))).append("|"));

        stringBuilder.append("\n");
    }

    private void appendColumnTypes() {

        stringBuilder.append("|").append(formatNumber(null));
        stringBuilder.append("|");

        table.columns().stream()
                .map(Column::getColumnType)
                .map(PrinterUtils::valueToString)
                .map(String::toUpperCase)
                .forEach(type -> stringBuilder.append(formatCell(new StringValue(type))).append("|"));

        stringBuilder.append("\n");
    }

    private void appendRows() {

        var array = new ArrayList<>(table.data());
        var rowsCount = Math.min(rowsCount(), rowsMaxCount);

        for (int i = 0; i < rowsCount; i++) {
            var row = array.get(i);
            stringBuilder.append("|").append(formatNumber(i + 1));
            stringBuilder.append("|");
            row.values().forEach(val -> stringBuilder.append(formatCell(val)).append("|"));
            stringBuilder.append("\n");
        }
    }

    private void checkForOverLimit() {
        if (rowsMaxCount < rowsCount()) {
            stringBuilder.append("|").append(PrinterUtils.fixedWidth("... ", numberColumnWidth()));
            stringBuilder.append("|");
            IntStream.range(0, columnsCount())
                    .forEach(i -> stringBuilder.append(formatCell(new StringValue("..."))).append("|"));
            stringBuilder.append("\n");
        }
    }

    private String formatCell(Value<?> entry) {
        return PrinterUtils.fixedWidth(entry.stringify() + " ", columnWidth);
    }

    private String formatNumber(Integer number) {
        if (Objects.isNull(number)) {
            return PrinterUtils.fixedWidth("", numberColumnWidth());
        }
        return PrinterUtils.fixedWidth(number + " ", numberColumnWidth());
    }

    private void delimiterRow() {

        stringBuilder.append("+").append("-".repeat(numberColumnWidth()));

        stringBuilder.append("+");
        for (int i = 0; i < columnsCount(); i++) {
            stringBuilder.append("-".repeat(columnWidth));
            stringBuilder.append("+");
        }
        stringBuilder.append("\n");
    }

    private Integer rowsCount() {
        return table.data().size();
    }

    private Integer columnsCount() {
        return table.columns().size();
    }

    private Integer numberColumnWidth() {
        return ("" + Math.min(table.data().size(), rowsMaxCount)).toString().length() + 2;
    }

}
