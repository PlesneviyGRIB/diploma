package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.structure.Table;

public interface Command {

    <T> T accept(Visitor<T> visitor);
    default void validate(Table table) {}

    interface Visitor<T> {
        T visit(SimpleCommand command);

        T visit(CalculatedCommand command);
    }
}
