package com.core.sqlTool.utils.printer;

import com.core.sqlTool.model.complexity.Calculator;
import com.core.sqlTool.model.visitor.CalculatorEntryCommandVisitor;
import com.core.sqlTool.utils.PrinterUtils;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CalculatorPrinter {

    public enum TableType {
        PRIMARY, JOIN, INNER
    }

    private final Calculator calculator;

    private final StringBuilder stringBuilder = new StringBuilder();

    private final String prefix;

    private final TableType tableType;

    public CalculatorPrinter(Calculator calculator) {
        this.calculator = calculator;
        this.prefix = "| ";
        this.tableType = TableType.PRIMARY;
    }

    @Override
    public String toString() {
        int length = getMaxRowLength();

        delimiterRow(length);
        appendHeader();
        delimiterRow(length);
        appendInfo();
        delimiterRow(length);

        return applyPrefix(stringBuilder.toString());
    }

    private void appendHeader() {
        var totalComplexity = 0;

        switch (tableType) {
            case PRIMARY -> stringBuilder.append("TOTAL COMPLEXITY: ").append(PrinterUtils.red(totalComplexity));
            case INNER -> stringBuilder.append("SUB TABLE COMPLEXITY: ").append(PrinterUtils.blue(totalComplexity));
            case JOIN -> stringBuilder.append("JOINED TABLE COMPLEXITY: ").append(PrinterUtils.blue(totalComplexity));
        }

        stringBuilder.append("\n");
    }

    private void appendInfo() {
        var data = calculator.entries().stream()
                .map(calculatorEntry -> calculatorEntry.getCommand().accept(new CalculatorEntryCommandVisitor(calculatorEntry)))
                .collect(Collectors.joining("\n"));

        stringBuilder.append(data).append("\n");
    }

    private void delimiterRow(int length) {
        stringBuilder.append("~".repeat(length)).append("\n");
    }

    private Integer getMaxRowLength() {
        appendHeader();
        var text = stringBuilder.toString();
        var headerLength = Arrays.stream(text.split("\n"))
                .mapToInt(String::length)
                .max().orElse(0) - 9;
        stringBuilder.setLength(0);


        appendInfo();
        var text1 = stringBuilder.toString();
        var infoLength = Arrays.stream(text1.split("\n"))
                .mapToInt(String::length)
                .max().orElse(0);
        stringBuilder.setLength(0);

        return Math.max(headerLength, infoLength);
    }

    private String applyPrefix(String text) {
        return Arrays.stream(text.split("\n"))
                .map(line -> "%s%s".formatted(prefix, line))
                .collect(Collectors.joining("\n"));
    }

}
