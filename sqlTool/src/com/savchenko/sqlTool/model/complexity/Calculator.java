package com.savchenko.sqlTool.model.complexity;

import com.savchenko.sqlTool.model.command.domain.ComplexCalculedCommand;
import com.savchenko.sqlTool.model.command.domain.SimpleCalculedCommand;
import com.savchenko.sqlTool.model.command.domain.SimpleCommand;
import com.savchenko.sqlTool.model.command.join.Join;

import java.util.LinkedList;
import java.util.List;

public class Calculator implements TotalCalculed {

    private final List<CalculatorEntry> entries = new LinkedList<>();

    public void log(SimpleCommand entry) {
        entries.add(new SimpleEntry(entry));
    }

    public void log(SimpleCalculedCommand entry, Integer complexity) {
        entries.add(new SimpleCalculedEntry(entry, complexity));
    }

    public void log(ComplexCalculedCommand entry, CalculedExpressionResult calculedExpressionResult, Integer count, boolean isContextSensitive) {
        entries.add(new ComplexCalculedEntry(entry, calculedExpressionResult, count, isContextSensitive));
    }

    public void log(Join entry, Calculator joinedTableCalculator, Integer remainderSize, CalculedExpressionResult calculedExpressionResult, Integer count, boolean isContextSensitive) {
        entries.add(new JoinCalculedEntry(entry, joinedTableCalculator, remainderSize, calculedExpressionResult, count, isContextSensitive));
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

    public Calculator instanceWithTakenLazinessIntoAccount() {
        // TODO
        return this;
    }

}
