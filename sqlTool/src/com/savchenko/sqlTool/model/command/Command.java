package com.savchenko.sqlTool.model.command;

public interface Command {

    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(SimpleCommand command);

        T visit(CalculatedCommand command);
    }
}
