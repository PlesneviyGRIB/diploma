package com.savchenko.sqlTool.model.complexity;

import com.savchenko.sqlTool.model.command.domain.Command;

import static java.lang.String.format;

public interface CalculatorEntry extends TotalCalculed {

    String stringify(String prefix);

    default String stringifyCommand(Command command) {
        return command.getClass().getSimpleName().toUpperCase();
    }

    default String toRow(String prefix, String template, Object... values) {
        return format(format("%s", prefix) + template, values);
    }

}
