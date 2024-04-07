package com.savchenko.sqlTool.model.complexity;

import com.savchenko.sqlTool.model.command.domain.SimpleCalculedCommand;

import static java.lang.String.format;

public record SimpleCalculedEntry(SimpleCalculedCommand command, Integer value) implements CalculatorEntry {

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String stringify(String prefix) {
        return format("%s%s %d", prefix, stringifyType(command), value);
    }

}
