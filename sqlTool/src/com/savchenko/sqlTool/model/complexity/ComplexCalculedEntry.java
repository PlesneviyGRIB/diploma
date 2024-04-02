package com.savchenko.sqlTool.model.complexity;

import com.savchenko.sqlTool.model.command.domain.ComplexCalculedCommand;

public record ComplexCalculedEntry(ComplexCalculedCommand command, Integer value, Integer count) implements CalculatorEntry {

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}