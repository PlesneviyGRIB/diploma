package com.savchenko.sqlTool.model.complexity;

import com.savchenko.sqlTool.model.expression.Expression;

import java.util.List;

public record CalculedExpressionResult(Integer complexity, List<Calculator> calculators, Expression expression) {
    public CalculedExpressionResult normalize() {
        var resultComplexity = complexity == 0 ? 1: complexity;
        return new CalculedExpressionResult(resultComplexity, calculators, expression);
    }
}
