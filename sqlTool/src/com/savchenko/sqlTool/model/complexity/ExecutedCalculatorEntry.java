package com.savchenko.sqlTool.model.complexity;

import com.savchenko.sqlTool.model.command.domain.Command;

public abstract class ExecutedCalculatorEntry implements CalculatorEntry {

    protected final Command command;

    public ExecutedCalculatorEntry(Command command) {
        this.command = command;
    }

    public String stringifyCommand() {
        return command.getClass().getSimpleName().toUpperCase();
    }

    public Command getCommand() {
        return command;
    }

}
