package com.savchenko.sqlTool.model.complexity;

import com.savchenko.sqlTool.model.command.domain.Command;

public class SimpleCalculatorEntry extends ExecutedCalculatorEntry {

    private final Integer value;

    public SimpleCalculatorEntry(Command command, Integer value) {
        super(command);
        this.value = value;
    }

    @Override
    public String stringify(String prefix) {
        return toRow(prefix, "%s %d", stringifyCommand(), value);
    }

    @Override
    public Integer getTotalComplexity() {
        return value;
    }
}
