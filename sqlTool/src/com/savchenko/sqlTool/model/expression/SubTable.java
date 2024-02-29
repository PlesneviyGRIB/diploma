package com.savchenko.sqlTool.model.expression;

import com.savchenko.sqlTool.model.command.Command;

import java.util.List;

public record SubTable(List<Command> commands) implements Expression {

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}
