package com.savchenko.sqlTool.model.complexity;

import com.savchenko.sqlTool.model.command.domain.ComplexCalculedCommand;
import com.savchenko.sqlTool.model.command.domain.SimpleCalculedCommand;
import com.savchenko.sqlTool.model.command.domain.SimpleCommand;
import com.savchenko.sqlTool.model.command.join.Join;

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

    public void log(ComplexCalculedCommand entry, CalculedExpressionEntry calculedExpressionEntry, Integer count, boolean isContextSensitive) {
        entries.add(new ComplexCalculedEntry(entry, calculedExpressionEntry, count, isContextSensitive));
    }

    public void log(Join entry, Calculator joinedTableCalculator, Integer remainderSize, CalculedExpressionEntry calculedExpressionEntry, Integer count, boolean isContextSensitive) {
        entries.add(new JoinCalculedEntry(entry, joinedTableCalculator, remainderSize, calculedExpressionEntry, count, isContextSensitive));
    }

    public List<CalculatorEntry> getEntries() {
        return entries;
    }

    public Integer getTotalComplexity() {
        return entries.stream()
                .map(e -> e.accept(new CalculatorEntry.Visitor<Integer>() {

                    @Override
                    public Integer visit(SimpleEntry entry) {
                        return 0;
                    }

                    @Override
                    public Integer visit(SimpleCalculedEntry entry) {
                        return entry.value();
                    }

                    @Override
                    public Integer visit(ComplexCalculedEntry entry) {
                        var cEntry = entry.calculedExpressionEntry();
                        var totalExpressionComplicity = cEntry.complexity();

                        return entry.isContextSensitive() ? totalExpressionComplicity * entry.count() : totalExpressionComplicity + entry.count();
                    }

                    @Override
                    public Integer visit(JoinCalculedEntry entry) {
                        var cEntry = entry.calculedExpressionEntry();
                        var totalExpressionComplicity = cEntry.complexity();

                        return entry.calculator().getTotalComplexity() + entry.remainderSize() +
                                (entry.isContextSensitive() ? totalExpressionComplicity * entry.count() : totalExpressionComplicity + entry.count());
                    }

                })).reduce(0, Integer::sum);
    }

}
