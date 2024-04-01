package com.savchenko.sqlTool.model.complexity;

public interface CalculatorEntry {

    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {

        T visit(SimpleEntry entry);

        T visit(SimpleCalculedEntry entry);

        T visit(ComplexCalculedEntry entry);

    }
}
