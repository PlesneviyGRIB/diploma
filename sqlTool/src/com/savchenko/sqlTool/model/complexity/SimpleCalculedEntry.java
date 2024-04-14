package com.savchenko.sqlTool.model.complexity;

import com.savchenko.sqlTool.model.command.domain.SimpleCalculedCommand;

public record SimpleCalculedEntry(SimpleCalculedCommand command, Integer value) implements CalculatorEntry {

    @Override
    public String stringify(String prefix) {
        return toRow(prefix, "%s %d", stringifyCommand(command), value);
    }

    @Override
    public String stringifyCached(String prefix) {
        return toRow(prefix, "%s %s", stringifyCommand(command), stringifyCachedResult());
    }

    @Override
    public Integer getTotalComplexity() {
        return value;
    }
}
