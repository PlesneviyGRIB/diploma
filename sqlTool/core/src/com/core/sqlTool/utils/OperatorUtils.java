package com.core.sqlTool.utils;


import com.client.sqlTool.expression.Operator;

import java.util.List;

import static com.client.sqlTool.expression.Operator.*;

public class OperatorUtils {

    public static boolean isLogic(Operator operator) {
        var operators = List.of(AND, BETWEEN, EXISTS, IN, OR, IS_NULL, EQ, NOT_EQ, GREATER_OR_EQ, LESS_OR_EQ, GREATER, LESS, NOT, LIKE);
        return operators.stream().anyMatch(o -> o.equals(operator));
    }

    public static boolean isArithmetic(Operator operator) {
        var operators = List.of(PLUS, MINUS, MULTIPLY, DIVISION, MOD);
        return operators.stream().anyMatch(o -> o.equals(operator));
    }

    public static boolean isUnary(Operator operator) {
        var operators = List.of(EXISTS, IS_NULL, NOT);
        return operators.stream().anyMatch(o -> o.equals(operator));
    }

    public static boolean isBinary(Operator operator) {
        var operators = List.of(AND, IN, OR, EQ, NOT_EQ, GREATER_OR_EQ, LESS_OR_EQ, GREATER, LESS, PLUS, MINUS, MULTIPLY, DIVISION, MOD, LIKE);
        return operators.stream().anyMatch(o -> o.equals(operator));
    }

    public static boolean isTernary(Operator operator) {
        var operators = List.of(BETWEEN);
        return operators.stream().anyMatch(o -> o.equals(operator));
    }

}
