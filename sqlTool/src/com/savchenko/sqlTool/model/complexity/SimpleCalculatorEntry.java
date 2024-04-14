package com.savchenko.sqlTool.model.complexity;

import com.savchenko.sqlTool.model.command.domain.SimpleCalculedCommand;

public record SimpleCalculatorEntry(SimpleCalculedCommand command, Integer value) implements CalculatorEntry {

    @Override
    public String stringify(String prefix) {
        return toRow(prefix, "%s %d", stringifyCommand(command), value);
    }

    @Override
    public Integer getTotalComplexity() {
        return value;
    }
}
