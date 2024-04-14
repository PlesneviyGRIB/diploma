package com.savchenko.sqlTool.model.complexity;

import java.util.LinkedList;
import java.util.List;

public class Calculator implements TotalCalculed {

    private final List<CalculatorEntry> entries = new LinkedList<>();

    public void log(CalculatorEntry calculatorEntry) {
        entries.add(calculatorEntry);
    }

    public List<CalculatorEntry> getEntries() {
        return entries;
    }

    @Override
    public Integer getTotalComplexity() {
        return entries.stream()
                .map(TotalCalculed::getTotalComplexity)
                .reduce(0, Integer::sum);
    }

    public Calculator withLaziness() {
        // TODO
        return this;
    }

}
