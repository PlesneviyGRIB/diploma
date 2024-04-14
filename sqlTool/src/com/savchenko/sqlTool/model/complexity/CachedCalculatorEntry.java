package com.savchenko.sqlTool.model.complexity;

import com.savchenko.sqlTool.model.command.domain.Command;

public record CachedCalculatorEntry(Command command, Integer complexity) implements CalculatorEntry {

    @Override
    public String stringify(String prefix) {
        return toRow(prefix, "%s CACHED(%d -> 0)", stringifyCommand(command), complexity);
    }

    @Override
    public Integer getTotalComplexity() {
        return 0;
    }

}
