package com.core.sqlTool.model.complexity;

import com.core.sqlTool.model.expression.Value;
import lombok.Getter;

import java.util.List;
import java.util.function.Supplier;

public class CalculatedExpressionResult {

    private final Supplier<CalculatedExpressionResult> calculatedExpressionResultSupplier;

    private Value<?> value;

    @Getter
    private Integer complexity = 0;

    private List<Calculator> calculators;

    private CalculatedExpressionResult(Supplier<CalculatedExpressionResult> calculatedExpressionResultSupplier) {
        this.calculatedExpressionResultSupplier = calculatedExpressionResultSupplier;
    }

    public Value<?> getValue() {
        if (value == null) {
            var calculatedExpressionResult = calculatedExpressionResultSupplier.get();
            value = calculatedExpressionResult.getValue();
            complexity = calculatedExpressionResult.getComplexity();
        }
        return value;
    }

    public CalculatedExpressionResult merge(CalculatedExpressionResult calculatedExpressionResult) {

        var result = new CalculatedExpressionResult(null);

        result.value = value;
        result.complexity = complexity + calculatedExpressionResult.getComplexity();

        return result;
    }

    public static CalculatedExpressionResult lazy(Supplier<CalculatedExpressionResult> valueSupplier) {
        return new CalculatedExpressionResult(valueSupplier);
    }

    public static CalculatedExpressionResult eager(Value<?> value, Integer complexity) {

        var calculatedExpressionResult = new CalculatedExpressionResult(null);

        calculatedExpressionResult.value = value;
        calculatedExpressionResult.complexity = complexity;

        return calculatedExpressionResult;
    }

}
