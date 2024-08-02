package com.core.sqlTool.utils.printer;

import com.core.sqlTool.model.complexity.Calculator;
import com.core.sqlTool.utils.PrinterUtils;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Comparator;

import static java.lang.String.format;

@RequiredArgsConstructor
public class CalculatorPrinter {

    public enum TableType {
        PRIMARY, JOIN, INNER
    }

    private final Calculator calculator;

    private final StringBuilder sb = new StringBuilder();

    private final String prefix;

    private final TableType tableType;

    public CalculatorPrinter(Calculator calculator) {
        this.calculator = calculator;
        this.prefix = "| ";
        this.tableType = TableType.PRIMARY;
    }

    @Override
    public String toString() {
        sb.setLength(0);

        appendHeader();
        appendInfo();

        int length = Math.max(getMaxRowLength() - prefix.length(), 0);
        sb.setLength(0);

        delimiterRow(length);
        sb.append("\n");
        appendHeader();
        delimiterRow(length);
        sb.append("\n");
        appendInfo();
        delimiterRow(length);

        return sb.toString();
    }

    private void appendHeader() {
        var totalComplexity = 0;

        switch (tableType) {
            case PRIMARY -> sb.append(format("%sTOTAL COMPLEXITY: %s", prefix, PrinterUtils.red(totalComplexity)));
            case JOIN -> sb.append(format("%sJOINED TABLE COMPLEXITY: %s", prefix, PrinterUtils.blue(totalComplexity)));
            case INNER -> sb.append(format("%sSUB TABLE COMPLEXITY: %s", prefix, PrinterUtils.blue(totalComplexity)));
        }

        sb.append("\n");
    }

    private void appendInfo() {
//        var data = calculator.entries().stream()
//                .map(c -> c.stringify(prefix))
//                .collect(Collectors.joining("\n"));
//
//        sb.append(data).append("\n");
    }

    private void delimiterRow(int length) {
        sb.append(prefix).append("~".repeat(length));
    }

    private Integer getMaxRowLength() {
        return Arrays.stream(sb.toString().split("\n"))
                .map(String::length)
                .max(Comparator.naturalOrder()).orElse(0);
    }

}
