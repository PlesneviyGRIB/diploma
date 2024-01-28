package com.savchenko.sqlTool.model.operator;

public interface Operator {
    <T> T accept(Visitor<T> visitor);
    interface Visitor<T> {
        T visit(EqOperator operator);
        T visit(LogicOperator operator);
        T visit(ArithmeticOperator operator);
    }
}
