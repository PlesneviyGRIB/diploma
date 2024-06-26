package com.savchenko.sqlTool.model.complexity;

import com.savchenko.sqlTool.model.expression.Expression;

import java.util.List;

public record CalculatedExpressionResult(Integer complexity, List<Calculator> calculators, Expression expression) {
    public CalculatedExpressionResult normalize() {
        var resultComplexity = complexity == 0 ? 1 : complexity;
        return new CalculatedExpressionResult(resultComplexity, calculators, expression);
    }
}
