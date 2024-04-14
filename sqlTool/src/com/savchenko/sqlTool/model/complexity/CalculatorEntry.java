package com.savchenko.sqlTool.model.complexity;

import com.savchenko.sqlTool.model.command.domain.Command;

import static java.lang.String.format;

public interface CalculatorEntry extends TotalCalculed {

    String stringify(String prefix);

    String stringifyCached(String prefix);

    default String stringifyCommand(Command command) {
        return command.getClass().getSimpleName().toUpperCase();
    }

    default String toRow(String prefix, String template, Object... values) {
        return format(format("%s", prefix) + template, values);
    }

    default String stringifyCachedResult() {
        if (this instanceof SimpleEntry) {
            return "-";
        }
        return format("CACHED(%d -> 0)", getTotalComplexity());
    }

}
