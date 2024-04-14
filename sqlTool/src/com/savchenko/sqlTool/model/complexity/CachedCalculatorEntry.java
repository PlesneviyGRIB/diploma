package com.savchenko.sqlTool.model.complexity;

public record CachedCalculatorEntry(ExecutedCalculatorEntry calculatorEntry) implements CalculatorEntry {

    @Override
    public String stringify(String prefix) {
        return toRow(prefix, "%s CACHED(%d -> 0)", calculatorEntry.stringifyCommand(), calculatorEntry.getTotalComplexity());
    }

    @Override
    public Integer getTotalComplexity() {
        return 0;
    }

}
