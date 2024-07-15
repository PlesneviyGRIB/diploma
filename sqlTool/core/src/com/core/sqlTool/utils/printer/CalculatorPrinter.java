package com.core.sqlTool.utils.printer;

import com.core.sqlTool.model.complexity.Calculator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class CalculatorPrinter extends Printer<Calculator> {

    public enum TableType {
        PRIMARY, JOIN, INNER
    }

    private final String prefix;

    private final TableType tableType;

    public CalculatorPrinter(Calculator domain) {
        super(domain);
        this.prefix = "| ";
        this.tableType = TableType.PRIMARY;
    }

    public CalculatorPrinter(Calculator domain, String prefix, TableType tableType) {
        super(domain);
        this.prefix = prefix;
        this.tableType = tableType;
    }

    @Override
    protected void buildString() {
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
    }

    private void appendHeader() {
        var totalComplexity = domain.getTotalComplexity();

        switch (tableType) {
            case PRIMARY -> sb.append(format("%sTOTAL COMPLEXITY: %s", prefix, red(totalComplexity)));
            case JOIN -> sb.append(format("%sJOINED TABLE COMPLEXITY: %s", prefix, blue(totalComplexity)));
            case INNER -> sb.append(format("%sSUB TABLE COMPLEXITY: %s", prefix, blue(totalComplexity)));
        }

        var fullComplexity = domain.getFullComplexity();
        var cacheDelta = fullComplexity - totalComplexity;

        if (cacheDelta > 0) {
            sb.append(green(format("    CACHE INFLUENCE: %s (%s -> %s)", cacheDelta, fullComplexity, totalComplexity)));
        }

        sb.append("\n");
    }

    private void appendInfo() {
        var data = domain.getEntries().stream()
                .map(c -> c.stringify(prefix))
                .collect(Collectors.joining("\n"));

        sb.append(data).append("\n");
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
