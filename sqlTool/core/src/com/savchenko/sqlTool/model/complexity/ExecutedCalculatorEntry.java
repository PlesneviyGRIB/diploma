package com.savchenko.sqlTool.model.complexity;

import com.savchenko.sqlTool.model.command.domain.Command;

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
