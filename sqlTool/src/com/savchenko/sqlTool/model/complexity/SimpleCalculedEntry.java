package com.savchenko.sqlTool.model.complexity;

import com.savchenko.sqlTool.model.command.domain.SimpleCalculedCommand;

public record SimpleCalculedEntry(SimpleCalculedCommand command, Integer value) implements CalculatorEntry {

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}
