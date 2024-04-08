package com.savchenko.sqlTool.model.visitor;

import com.savchenko.sqlTool.model.complexity.CalculedExpressionResult;
import com.savchenko.sqlTool.model.domain.Column;
import com.savchenko.sqlTool.model.domain.ExternalRow;
import com.savchenko.sqlTool.model.expression.*;
import com.savchenko.sqlTool.model.resolver.Resolver;
import org.apache.commons.collections4.ListUtils;

import java.util.List;

import static com.savchenko.sqlTool.model.operator.Operator.IN;

public class ExpressionComplexityCalculator implements Expression.Visitor<CalculedExpressionResult> {

    private final Resolver resolver;

    private final ExternalRow externalRow;

    public ExpressionComplexityCalculator(Resolver resolver, ExternalRow externalRow) {
        this.resolver = resolver;
        this.externalRow = externalRow;
    }

    @Override
    public CalculedExpressionResult visit(ExpressionList list) {
        return new CalculedExpressionResult(0, List.of(), list);
    }

    @Override
    public CalculedExpressionResult visit(SubTable table) {
        var result = resolver.resolve(table.commands(), externalRow);
        return new CalculedExpressionResult(result.calculator().getTotalComplexity(), List.of(result.calculator()), table);
    }

    @Override
    public CalculedExpressionResult visit(Column column) {
        return new CalculedExpressionResult(1, List.of(), column);
    }

    @Override
    public CalculedExpressionResult visit(UnaryOperation operation) {
        var calculedExpressionEntry = operation.expression().accept(this);
        return new CalculedExpressionResult(calculedExpressionEntry.complexity() + 1, calculedExpressionEntry.calculators(), operation);
    }

    @Override
    public CalculedExpressionResult visit(BinaryOperation operation) {

        var left = operation.left().accept(this);
        var right = operation.right().accept(this);
        Integer additionalComplexity = 0;

        if(operation.operator() == IN) {

            if(right.expression() instanceof ExpressionList expressionList) {
                additionalComplexity = expressionList.expressions().size();
            }

            if(right.expression() instanceof SubTable subTable) {
                var result = resolver.resolve(subTable.commands(), externalRow);
                additionalComplexity = result.table().data().size();
            }
        }

        return new CalculedExpressionResult(left.complexity() + right.complexity() + 1 + additionalComplexity,
                ListUtils.union(left.calculators(), right.calculators()), operation);
    }

    @Override
    public CalculedExpressionResult visit(TernaryOperation operation) {
        var first = operation.first().accept(this);
        var second = operation.second().accept(this);
        var third = operation.third().accept(this);

        Integer additionalComplexity = 0;

        var calculatedSecond = second.expression().accept(new ExpressionCalculator(resolver, externalRow));

        if(calculatedSecond instanceof StringValue secondStringValue) {
            var thirdStringValue = (StringValue) third.expression().accept(new ExpressionCalculator(resolver, externalRow));

            additionalComplexity += secondStringValue.value().length() + thirdStringValue.value().length();
        }

        return new CalculedExpressionResult(
                first.complexity() + second.complexity() + third.complexity() + additionalComplexity,
                ListUtils.union(ListUtils.union(first.calculators(), second.calculators()), third.calculators()),
                operation
        );
    }

    @Override
    public CalculedExpressionResult visit(NullValue value) {
        return new CalculedExpressionResult(0, List.of(), value);
    }

    @Override
    public CalculedExpressionResult visit(StringValue value) {
        return new CalculedExpressionResult(0, List.of(), value);
    }

    @Override
    public CalculedExpressionResult visit(BooleanValue value) {
        return new CalculedExpressionResult(0, List.of(), value);
    }

    @Override
    public CalculedExpressionResult visit(IntegerNumber value) {
        return new CalculedExpressionResult(0, List.of(), value);
    }

    @Override
    public CalculedExpressionResult visit(LongNumber value) {
        return new CalculedExpressionResult(0, List.of(), value);
    }

    @Override
    public CalculedExpressionResult visit(FloatNumber value) {
        return new CalculedExpressionResult(0, List.of(), value);
    }

    @Override
    public CalculedExpressionResult visit(DoubleNumber value) {
        return new CalculedExpressionResult(0, List.of(), value);
    }

    @Override
    public CalculedExpressionResult visit(BigDecimalNumber value) {
        return new CalculedExpressionResult(0, List.of(), value);
    }

    @Override
    public CalculedExpressionResult visit(TimestampValue value) {
        return new CalculedExpressionResult(0, List.of(), value);
    }

}
