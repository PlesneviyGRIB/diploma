package com.savchenko.sqlTool.model.complexity;

import com.savchenko.sqlTool.model.command.From;
import com.savchenko.sqlTool.model.command.domain.SimpleCommand;

import static java.lang.String.format;

public record SimpleEntry(SimpleCommand command) implements CalculatorEntry {

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String stringify(String prefix) {
        if(command instanceof From from) {
            return format("%s%s[%s] -", prefix, stringifyType(from), from.getTableName());
        }
        return format("%s%s -", prefix, stringifyType(command));
    }

}
