package com.client.sqlTool.expression;

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

}
