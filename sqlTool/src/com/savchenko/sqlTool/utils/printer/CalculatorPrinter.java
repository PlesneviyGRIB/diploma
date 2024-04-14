package com.savchenko.sqlTool.utils.printer;

import com.savchenko.sqlTool.model.complexity.Calculator;

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
        var complexity = domain.getTotalComplexity();

        switch (tableType) {
            case PRIMARY -> sb.append(format("%sTOTAL COMPLEXITY: \u001B[34m%s\u001B[0m \n", prefix, complexity));
            case JOIN -> sb.append(format("%sJOINED TABLE COMPLEXITY: \u001B[32m%s\u001B[0m\n", prefix, complexity));
            case INNER -> sb.append(format("%sSUB TABLE COMPLEXITY: \u001B[32m%s\u001B[0m\n", prefix, complexity));
        }
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
