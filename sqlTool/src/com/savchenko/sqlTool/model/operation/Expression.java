package com.savchenko.sqlTool.model.operation;

import com.savchenko.sqlTool.model.structure.Column;

public interface Expression {
    <T> T accept(Visitor<T> visitor);
    interface Visitor<T> {
        T visit(Comparable<?> value);
        T visit(Column column);
        T visit(UnaryOperation operation);
        T visit(BinaryOperation operation);
    }
}
