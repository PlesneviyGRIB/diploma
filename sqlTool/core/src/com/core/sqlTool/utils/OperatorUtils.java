package com.core.sqlTool.utils;


import com.client.sqlTool.expression.Operator;

import java.util.stream.Stream;

import static com.client.sqlTool.expression.Operator.*;

public class OperatorUtils {

    public static boolean isLogic(Operator operator) {
        var operatorStream = Stream.of(AND, BETWEEN, EXISTS, IN, OR, IS_NULL, EQ, NOT_EQ, GREATER_OR_EQ, LESS_OR_EQ, GREATER, LESS, NOT, LIKE);
        return operatorStream.anyMatch(o -> o.equals(operator));
    }

    public static boolean isArithmetic(Operator operator) {
        var operatorStream = Stream.of(PLUS, MINUS, MULTIPLY, DIVISION, MOD);
        return operatorStream.anyMatch(o -> o.equals(operator));
    }

    public static boolean isUnary(Operator operator) {
        var operatorStream = Stream.of(EXISTS, IS_NULL, NOT);
        return operatorStream.anyMatch(o -> o.equals(operator));
    }

    public static boolean isBinary(Operator operator) {
        var operatorStream = Stream.of(AND, IN, OR, EQ, NOT_EQ, GREATER_OR_EQ, LESS_OR_EQ, GREATER, LESS, PLUS, MINUS, MULTIPLY, DIVISION, MOD, LIKE);
        return operatorStream.anyMatch(o -> o.equals(operator));
    }

    public static boolean isTernary(Operator operator) {
        var operatorStream = Stream.of(BETWEEN);
        return operatorStream.anyMatch(o -> o.equals(operator));
    }

}
