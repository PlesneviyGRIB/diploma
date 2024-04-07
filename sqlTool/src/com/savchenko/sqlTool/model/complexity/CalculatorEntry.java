package com.savchenko.sqlTool.model.complexity;

import com.savchenko.sqlTool.model.command.domain.Command;

public interface CalculatorEntry {

    <T> T accept(Visitor<T> visitor);

    String stringify(String prefix);

    default String stringifyType(Command command) {
        return command.getClass().getSimpleName().toUpperCase();
    }

    interface Visitor<T> {

        T visit(SimpleEntry entry);

        T visit(SimpleCalculedEntry entry);

        T visit(ComplexCalculedEntry entry);

        T visit(JoinCalculedEntry entry);

    }
}
