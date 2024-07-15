package com.savchenko.sqlTool.model.operator;

import java.util.List;

public enum Operator {
    AND("and"),
    BETWEEN("between"),
    EXISTS("exists"),
    NOT("not"),
    IN("in"),
    OR("or"),
    IS_NULL("is null"),
    LIKE("like"),

    EQ("="),
    NOT_EQ("!="),
    GREATER_OR_EQ(">="),
    LESS_OR_EQ("<="),
    GREATER(">"),
    LESS("<"),

    PLUS("+"),
    MINUS("-"),
    MULTIPLY("*"),
    DIVISION("/"),
    MOD("%");

    public final String designator;
    Operator(String designator) {
        this.designator = designator;
    }

    public boolean isLogic() {
        var operators = List.of(AND, BETWEEN, EXISTS, IN, OR, IS_NULL, EQ, NOT_EQ, GREATER_OR_EQ, LESS_OR_EQ, GREATER, LESS, NOT, LIKE);
        return operators.stream().anyMatch(o -> o.equals(this));
    }

    public boolean isArithmetic() {
        var operators = List.of(PLUS, MINUS, MULTIPLY, DIVISION, MOD);
        return operators.stream().anyMatch(o -> o.equals(this));
    }

    public boolean isUnary() {
        var operators = List.of(EXISTS, IS_NULL, NOT);
        return operators.stream().anyMatch(o -> o.equals(this));
    }

    public boolean isBinary() {
        var operators = List.of(AND, IN, OR, EQ, NOT_EQ, GREATER_OR_EQ, LESS_OR_EQ, GREATER, LESS, PLUS, MINUS, MULTIPLY, DIVISION, MOD, LIKE);
        return operators.stream().anyMatch(o -> o.equals(this));
    }

    public boolean isTernary() {
        var operators = List.of(BETWEEN);
        return operators.stream().anyMatch(o -> o.equals(this));
    }
}
