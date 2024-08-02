package com.client.sqlTool.expression;

import lombok.Getter;

@Getter
public enum Operator {

    AND("and"),
    BETWEEN("between"),
    EXISTS("exists"),
    NOT("not"),
    IN("in"),
    OR("or"),
    IS_NULL("is null"),
    IS_NOT_NULL("is not null"),
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

    private final java.lang.String designator;

    Operator(java.lang.String designator) {
        this.designator = designator;
    }

}
