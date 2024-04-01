package com.savchenko.sqlTool.model.complexity;

import com.savchenko.sqlTool.model.command.domain.SimpleCommand;

public record SimpleEntry(SimpleCommand command) implements CalculatorEntry {

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}
