package com.savchenko.sqlTool.model.visitor;

import com.savchenko.sqlTool.model.complexity.CalculedExpressionEntry;
import com.savchenko.sqlTool.model.domain.Column;
import com.savchenko.sqlTool.model.domain.ExternalRow;
import com.savchenko.sqlTool.model.expression.*;
import com.savchenko.sqlTool.model.resolver.Resolver;
import org.apache.commons.collections4.ListUtils;

import java.util.List;

import static com.savchenko.sqlTool.model.operator.Operator.IN;

public class ExpressionComplicityCalculator implements Expression.Visitor<CalculedExpressionEntry> {

    private final Resolver resolver;

    private final ExternalRow externalRow;

    public ExpressionComplicityCalculator(Resolver resolver, ExternalRow externalRow) {
        this.resolver = resolver;
        this.externalRow = externalRow;
    }

    @Override
    public CalculedExpressionEntry visit(ExpressionList list) {
        return new CalculedExpressionEntry(0, List.of(), list);
    }

    @Override
    public CalculedExpressionEntry visit(SubTable table) {
        var result = resolver.resolve(table.commands(), externalRow);
        return new CalculedExpressionEntry(result.calculator().getTotalComplexity(), List.of(result.calculator()), table);
    }

    @Override
    public CalculedExpressionEntry visit(Column column) {
        return new CalculedExpressionEntry(1, List.of(), column);
    }

    @Override
    public CalculedExpressionEntry visit(UnaryOperation operation) {
        var calculedExpressionEntry = operation.expression().accept(this);
        return new CalculedExpressionEntry(calculedExpressionEntry.complexity() + 1, calculedExpressionEntry.calculators(), operation);
    }

    @Override
    public CalculedExpressionEntry visit(BinaryOperation operation) {

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

        return new CalculedExpressionEntry(left.complexity() + right.complexity() + 1 + additionalComplexity,
                ListUtils.union(left.calculators(), right.calculators()), operation);
    }

    @Override
    public CalculedExpressionEntry visit(TernaryOperation operation) {
        var first = operation.first().accept(this);
        var second = operation.second().accept(this);
        var third = operation.third().accept(this);

        Integer additionalComplexity = 0;

        var calculatedSecond = second.expression().accept(new ExpressionCalculator(resolver, externalRow));

        if(calculatedSecond instanceof StringValue secondStringValue) {
            var thirdStringValue = (StringValue) third.expression().accept(new ExpressionCalculator(resolver, externalRow));

            additionalComplexity += secondStringValue.value().length() + thirdStringValue.value().length();
        }

        return new CalculedExpressionEntry(
                first.complexity() + second.complexity() + third.complexity() + additionalComplexity,
                ListUtils.union(ListUtils.union(first.calculators(), second.calculators()), third.calculators()),
                operation
        );
    }

    @Override
    public CalculedExpressionEntry visit(NullValue value) {
        return new CalculedExpressionEntry(0, List.of(), value);
    }

    @Override
    public CalculedExpressionEntry visit(StringValue value) {
        return new CalculedExpressionEntry(0, List.of(), value);
    }

    @Override
    public CalculedExpressionEntry visit(BooleanValue value) {
        return new CalculedExpressionEntry(0, List.of(), value);
    }

    @Override
    public CalculedExpressionEntry visit(IntegerNumber value) {
        return new CalculedExpressionEntry(0, List.of(), value);
    }

    @Override
    public CalculedExpressionEntry visit(LongNumber value) {
        return new CalculedExpressionEntry(0, List.of(), value);
    }

    @Override
    public CalculedExpressionEntry visit(FloatNumber value) {
        return new CalculedExpressionEntry(0, List.of(), value);
    }

    @Override
    public CalculedExpressionEntry visit(DoubleNumber value) {
        return new CalculedExpressionEntry(0, List.of(), value);
    }

    @Override
    public CalculedExpressionEntry visit(BigDecimalNumber value) {
        return new CalculedExpressionEntry(0, List.of(), value);
    }

    @Override
    public CalculedExpressionEntry visit(TimestampValue value) {
        return new CalculedExpressionEntry(0, List.of(), value);
    }

}
