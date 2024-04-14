package com.savchenko.sqlTool.model.complexity;

import com.savchenko.sqlTool.model.command.From;
import com.savchenko.sqlTool.model.command.domain.SimpleCommand;

public record SimpleEntry(SimpleCommand command) implements CalculatorEntry {

    @Override
    public String stringify(String prefix) {
        if (command instanceof From from) {
            return toRow(prefix, "%s[%s] -", stringifyCommand(from), from.getTableName());
        }
        return toRow(prefix, "%s -", stringifyCommand(command));
    }

    @Override
    public Integer getTotalComplexity() {
        return 0;
    }
}
