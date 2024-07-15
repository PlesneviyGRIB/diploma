package com.savchenko.sqlTool.model.complexity;

import com.savchenko.sqlTool.model.command.domain.Command;

public class SimpleCalculatorEntry extends ExecutedCalculatorEntry {

    public SimpleCalculatorEntry(Command command) {
        super(command);
    }

    @Override
    public String stringify(String prefix) {
        return toRow(prefix, "%s %d", stringifyCommand(), counter.get());
    }

    @Override
    public Integer getTotalComplexity() {
        return counter.get();
    }

    @Override
    public Integer getFullComplexity() {
        return counter.get();
    }
}
