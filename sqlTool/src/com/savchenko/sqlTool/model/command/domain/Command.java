package com.savchenko.sqlTool.model.command.domain;

public interface Command {

    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {

        T visit(SimpleCommand command);

        T visit(SimpleCalculedCommand command);

        T visit(ComplexCalculedCommand command);

    }

}
