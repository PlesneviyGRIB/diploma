package com.savchenko.sqlTool.utils.printer;

import com.savchenko.sqlTool.model.command.domain.Command;
import com.savchenko.sqlTool.model.complexity.*;

import java.util.function.Function;

import static java.lang.String.format;

public class CalculatorPrinter extends Printer<Calculator> {

    public CalculatorPrinter(Calculator domain) {
        super(domain);
    }

    @Override
    protected void buildString() {
        appendHeader();
        appendInfo();
    }

    private void appendHeader() {
        var total = domain.getEntries().stream()
                .map(e -> e.accept(new CalculatorEntry.Visitor<Integer>() {
                    @Override
                    public Integer visit(SimpleEntry entry) {
                        return 0;
                    }

                    @Override
                    public Integer visit(SimpleCalculedEntry entry) {
                        return entry.value();
                    }

                    @Override
                    public Integer visit(ComplexCalculedEntry entry) {
                        return entry.value();
                    }
                })).reduce(0, Integer::sum);

        sb.append(format("TOTAL COMPLEXITY: %s\n", total));
        sb.append("-".repeat(20)).append("\n");
    }

    private void appendInfo() {
        domain.getEntries().stream()
                .map(e -> e.accept(new CalculatorEntry.Visitor<String>() {

                    private final Function<Command, String> printer = command -> command.getClass().getSimpleName().toUpperCase();

                    @Override
                    public String visit(SimpleEntry entry) {
                        return format("%s -\n", printer.apply(entry.command()));
                    }

                    @Override
                    public String visit(SimpleCalculedEntry entry) {
                        return format("%s %d\n", printer.apply(entry.command()), entry.value());
                    }

                    @Override
                    public String visit(ComplexCalculedEntry entry) {
                        return format("%s %d\n", printer.apply(entry.command()), entry.value());
                    }
                })).forEach(sb::append);
    }

}
