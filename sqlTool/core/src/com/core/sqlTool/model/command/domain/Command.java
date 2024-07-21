package com.core.sqlTool.model.command.domain;

public interface Command {

    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {

        T visit(SimpleCommand command);

        T visit(SimpleCalculatedCommand command);

        T visit(ComplexCalculatedCommand command);

    }

}
