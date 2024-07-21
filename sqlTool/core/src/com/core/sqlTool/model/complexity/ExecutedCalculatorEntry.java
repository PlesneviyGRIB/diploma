package com.core.sqlTool.model.complexity;

import com.core.sqlTool.model.command.Command;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class ExecutedCalculatorEntry implements CalculatorEntry {

    protected final Command command;

    protected final AtomicInteger counter = new AtomicInteger();

    public ExecutedCalculatorEntry(Command command) {
        this.command = command;
    }

    public String stringifyCommand() {
        return command.getClass().getSimpleName().toUpperCase();
    }

    public Command getCommand() {
        return command;
    }

    @Override
    public void count(Object object) {
        counter.incrementAndGet();
    }
}