package com.savchenko.sqlTool.model.complexity;

import com.savchenko.sqlTool.model.command.From;
import com.savchenko.sqlTool.model.command.domain.SimpleCommand;

public class SimpleEntry extends ExecutedCalculatorEntry {

    public SimpleEntry(SimpleCommand command) {
        super(command);
    }

    @Override
    public String stringify(String prefix) {
        if (getCommand() instanceof From from) {
            return toRow(prefix, "%s[%s] -", stringifyCommand(), from.getTableName());
        }
        return toRow(prefix, "%s -", stringifyCommand());
    }

    @Override
    public Integer getTotalComplexity() {
        return 0;
    }

    @Override
    public Integer getFullComplexity() {
        return 0;
    }
}
