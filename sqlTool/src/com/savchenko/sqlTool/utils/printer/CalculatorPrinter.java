package com.savchenko.sqlTool.utils.printer;

import com.savchenko.sqlTool.model.complexity.Calculator;
import com.savchenko.sqlTool.model.complexity.CalculatorEntry;

import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class CalculatorPrinter extends Printer<Calculator> {

    private final String prefix;

    private final Boolean isSubTable;

    public CalculatorPrinter(Calculator domain) {
        super(domain);
        this.prefix = "| ";
        this.isSubTable = null;
    }

    public CalculatorPrinter(Calculator domain, String prefix, boolean isSubTable) {
        super(domain);
        this.prefix = prefix;
        this.isSubTable = isSubTable;
    }

    @Override
    protected void buildString() {
        appendHeader();
        appendInfo();
    }

    private void appendHeader() {
        var complexity = domain.getTotalComplexity();

        if(Objects.isNull(isSubTable)) {
            sb.append(format("%sTOTAL COMPLEXITY: \u001B[34m%s\u001B[0m \n", prefix, complexity));
            return;
        }

        if(isSubTable) {
            sb.append(format("%sSUB TABLE COMPLEXITY: \u001B[32m%s\u001B[0m\n", prefix, complexity));
        } else {
            sb.append(format("%sJOINED TABLE COMPLEXITY: \u001B[32m%s\u001B[0m\n", prefix, complexity));
        }
    }

    private void appendInfo() {
        var data = domain.getEntries().stream()
                .map(c -> c.stringify(prefix))
                .collect(Collectors.joining("\n"));

        sb.append(data);
    }

}
