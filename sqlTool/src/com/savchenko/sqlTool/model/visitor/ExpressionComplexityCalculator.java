package com.savchenko.sqlTool.model.visitor;

import com.savchenko.sqlTool.model.complexity.CalculatedExpressionResult;
import com.savchenko.sqlTool.model.domain.Column;
import com.savchenko.sqlTool.model.domain.ExternalRow;
import com.savchenko.sqlTool.model.expression.*;
import com.savchenko.sqlTool.model.resolver.Resolver;
import org.apache.commons.collections4.ListUtils;

import java.util.List;

import static com.savchenko.sqlTool.model.operator.Operator.IN;

public class ExpressionComplexityCalculator implements Expression.Visitor<CalculatedExpressionResult> {

    private final Resolver resolver;

    private final ExternalRow externalRow;

    public ExpressionComplexityCalculator(Resolver resolver, ExternalRow externalRow) {
        this.resolver = resolver;
        this.externalRow = externalRow;
    }

    @Override
    public CalculatedExpressionResult visit(ExpressionList list) {
        return new CalculatedExpressionResult(0, List.of(), list);
    }

    @Override
    public CalculatedExpressionResult visit(SubTable table) {
        var result = resolver.resolve(table.commands(), externalRow);
        return new CalculatedExpressionResult(result.calculator().getTotalComplexity(), List.of(result.calculator()), table);
    }

    @Override
    public CalculatedExpressionResult visit(Column column) {
        return new CalculatedExpressionResult(1, List.of(), column);
    }

    @Override
    public CalculatedExpressionResult visit(UnaryOperation operation) {
        var calculedExpressionEntry = operation.expression().accept(this);
        return new CalculatedExpressionResult(calculedExpressionEntry.complexity() + 1, calculedExpressionEntry.calculators(), operation);
    }

    @Override
    public CalculatedExpressionResult visit(BinaryOperation operation) {

        var left = operation.left().accept(this);
        var right = operation.right().accept(this);
        Integer additionalComplexity = 0;

        if (operation.operator() == IN) {

            if (right.expression() instanceof ExpressionList expressionList) {
                additionalComplexity = expressionList.expressions().size();
            }

            if (right.expression() instanceof SubTable subTable) {
                var result = resolver.resolve(subTable.commands(), externalRow);
                additionalComplexity = 0;
            }
        }

        return new CalculatedExpressionResult(left.complexity() + right.complexity() + 1 + additionalComplexity,
                ListUtils.union(left.calculators(), right.calculators()), operation);
    }

    @Override
    public CalculatedExpressionResult visit(TernaryOperation operation) {
        var first = operation.first().accept(this);
        var second = operation.second().accept(this);
        var third = operation.third().accept(this);

        Integer additionalComplexity = 0;

        var calculatedSecond = second.expression().accept(new ExpressionCalculator(resolver, externalRow));

        if (calculatedSecond instanceof StringValue secondStringValue) {
            var thirdStringValue = (StringValue) third.expression().accept(new ExpressionCalculator(resolver, externalRow));

            additionalComplexity += secondStringValue.value().length() + thirdStringValue.value().length();
        }

        return new CalculatedExpressionResult(
                first.complexity() + second.complexity() + third.complexity() + additionalComplexity,
                ListUtils.union(ListUtils.union(first.calculators(), second.calculators()), third.calculators()),
                operation
        );
    }

    @Override
    public CalculatedExpressionResult visit(NullValue value) {
        return new CalculatedExpressionResult(0, List.of(), value);
    }

    @Override
    public CalculatedExpressionResult visit(StringValue value) {
        return new CalculatedExpressionResult(0, List.of(), value);
    }

    @Override
    public CalculatedExpressionResult visit(BooleanValue value) {
        return new CalculatedExpressionResult(0, List.of(), value);
    }

    @Override
    public CalculatedExpressionResult visit(IntegerNumber value) {
        return new CalculatedExpressionResult(0, List.of(), value);
    }

    @Override
    public CalculatedExpressionResult visit(LongNumber value) {
        return new CalculatedExpressionResult(0, List.of(), value);
    }

    @Override
    public CalculatedExpressionResult visit(FloatNumber value) {
        return new CalculatedExpressionResult(0, List.of(), value);
    }

    @Override
    public CalculatedExpressionResult visit(DoubleNumber value) {
        return new CalculatedExpressionResult(0, List.of(), value);
    }

    @Override
    public CalculatedExpressionResult visit(BigDecimalNumber value) {
        return new CalculatedExpressionResult(0, List.of(), value);
    }

    @Override
    public CalculatedExpressionResult visit(TimestampValue value) {
        return new CalculatedExpressionResult(0, List.of(), value);
    }

}
