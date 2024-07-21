package com.core.sqlTool.model.complexity;

import com.core.sqlTool.model.command.SimpleCommand;
import com.core.sqlTool.model.command.TableAliasCommand;
import com.core.sqlTool.model.command.FromCommand;

public class SimpleEntry extends ExecutedCalculatorEntry {

    public SimpleEntry(SimpleCommand command) {
        super(command);
    }

    @Override
    public String stringify(String prefix) {
        if (getCommand() instanceof FromCommand from) {
            return toRow(prefix, "%s[%s] -", stringifyCommand(), from.tableName());
        }
        if (getCommand() instanceof TableAliasCommand alias) {
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
