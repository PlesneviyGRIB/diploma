package com.savchenko.sqlTool.model.complexity;

import com.savchenko.sqlTool.model.command.domain.ComplexCalculedCommand;
import com.savchenko.sqlTool.model.command.domain.SimpleCalculedCommand;
import com.savchenko.sqlTool.model.command.domain.SimpleCommand;

import java.util.LinkedList;
import java.util.List;

public class Calculator {

    private final List<CalculatorEntry> entries = new LinkedList<>();

    public void log(SimpleCommand entry) {
        entries.add(new SimpleEntry(entry));
    }

    public void log(SimpleCalculedCommand entry, Integer complexity) {
        entries.add(new SimpleCalculedEntry(entry, complexity));
    }

    public void log(ComplexCalculedCommand entry, Integer complexity) {
        entries.add(new ComplexCalculedEntry(entry, complexity));
    }

    public List<CalculatorEntry> getEntries() {
        return entries;
    }

}
