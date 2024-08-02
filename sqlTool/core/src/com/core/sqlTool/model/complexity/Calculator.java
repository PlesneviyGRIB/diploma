package com.core.sqlTool.model.complexity;

import java.util.LinkedList;
import java.util.List;

public class Calculator implements TotalCalculated {

    private final List<CalculatorEntry> entries = new LinkedList<>();

    public void log(CalculatorEntry calculatorEntry) {
        entries.add(calculatorEntry);
    }

    public List<CalculatorEntry> getEntries() {
        return entries;
    }

    @Override
    public Integer getTotalComplexity() {
        return 0;
//        return entries.stream()
//                .map(TotalCalculated::getTotalComplexity)
//                .reduce(0, Integer::sum);
    }

    @Override
    public Integer getFullComplexity() {
        return 0;
//        return entries.stream()
//                .map(TotalCalculated::getFullComplexity)
//                .reduce(0, Integer::sum);
    }

    public Calculator withLaziness() {
        // TODO
        return this;
    }

}
