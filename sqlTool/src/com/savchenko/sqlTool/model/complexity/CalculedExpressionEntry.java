package com.savchenko.sqlTool.model.complexity;

import com.savchenko.sqlTool.model.expression.Expression;

import java.util.List;

public record CalculedExpressionEntry(Integer complexity, List<Calculator> calculators, Expression expression) {
    public CalculedExpressionEntry normalize() {
        var resultComplicity = complexity == 0 ? 1: complexity;
        return new CalculedExpressionEntry(resultComplicity, calculators, expression);
    }
}
