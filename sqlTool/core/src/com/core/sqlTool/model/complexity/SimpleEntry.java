package com.core.sqlTool.model.complexity;

import com.core.sqlTool.model.command.domain.SimpleCommand;
import com.core.sqlTool.model.command.AliasCommand;
import com.core.sqlTool.model.command.FromCommand;

public class SimpleEntry extends ExecutedCalculatorEntry {

    public SimpleEntry(SimpleCommand command) {
        super(command);
    }

    @Override
    public String stringify(String prefix) {
        if (getCommand() instanceof FromCommand from) {
            return toRow(prefix, "%s[%s] -", stringifyCommand(), from.getTableName());
        }
        if (getCommand() instanceof AliasCommand alias) {
            return toRow(prefix, "%s[%s] -", stringifyCommand(), alias.alias());
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
