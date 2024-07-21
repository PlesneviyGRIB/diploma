package com.core.sqlTool.model.visitor;

import com.client.sqlTool.expression.Operator;
import com.core.sqlTool.model.complexity.CalculatedExpressionResult;
import com.core.sqlTool.model.domain.Column;
import com.core.sqlTool.model.domain.ExternalHeaderRow;
import com.core.sqlTool.model.domain.HeaderRow;
import com.core.sqlTool.model.expression.NumberValue;
import com.core.sqlTool.model.expression.*;
import com.core.sqlTool.model.resolver.Resolver;
import org.apache.commons.collections4.ListUtils;

import java.util.List;

public class ExpressionComplexityCalculator implements Expression.Visitor<CalculatedExpressionResult> {

    private final Resolver resolver;

    private final HeaderRow headerRow;

    private final ExternalHeaderRow externalRow;

    private final ExternalHeaderRow mergedExternalRow;

    public ExpressionComplexityCalculator(Resolver resolver, HeaderRow headerRow, ExternalHeaderRow externalRow) {
        this.resolver = resolver;
        this.headerRow = headerRow;
        this.externalRow = externalRow;
        this.mergedExternalRow = externalRow.merge(new ExternalHeaderRow(headerRow.getColumns(), headerRow.getRow()));
    }

    @Override
    public CalculatedExpressionResult visit(ValueList list) {
        return new CalculatedExpressionResult(0, List.of(), list);
    }

    @Override
    public CalculatedExpressionResult visit(SubTable table) {
        return visitSubTable(table, true);
    }

    private CalculatedExpressionResult visitSubTable(SubTable table, boolean fetchAll) {
        var result = resolver.resolve(table.commands(), mergedExternalRow);
        if (fetchAll) {
            result.lazyTable().fetch();
        } else {
            result.lazyTable().dataStream().findAny();
        }
        return new CalculatedExpressionResult(result.calculator().getTotalComplexity(), List.of(result.calculator()), table);
    }

    @Override
    public CalculatedExpressionResult visit(Column column) {
        return new CalculatedExpressionResult(1, List.of(), column);
    }

    @Override
    public CalculatedExpressionResult visit(UnaryOperation operation) {

        if (operation.operator() == Operator.EXISTS && operation.expression() instanceof SubTable subTable) {
            return visitSubTable(subTable, false);
        }

        var calculedExpressionEntry = operation.expression().accept(this);
        return new CalculatedExpressionResult(calculedExpressionEntry.complexity() + 1, calculedExpressionEntry.calculators(), operation);
    }

    @Override
    public CalculatedExpressionResult visit(BinaryOperation operation) {

        var left = operation.left().accept(this);
        var right = operation.right().accept(this);
        Integer additionalComplexity = 0;

        if (operation.operator() == Operator.IN) {

            if (right.expression() instanceof ValueList valueList) {
                additionalComplexity = valueList.expressions().size();
            }

            if (right.expression() instanceof SubTable subTable) {
                var result = resolver.resolve(subTable.commands(), mergedExternalRow);
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

        var calculatedSecond = second.expression().accept(new ExpressionCalculator(resolver, headerRow, externalRow));

        if (calculatedSecond instanceof StringValue secondStringValue) {
            var thirdStringValue = (StringValue) third.expression().accept(new ExpressionCalculator(resolver, headerRow, externalRow));

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
    public CalculatedExpressionResult visit(NumberValue value) {
        return new CalculatedExpressionResult(0, List.of(), value);
    }

    @Override
    public CalculatedExpressionResult visit(FloatNumberValue value) {
        return new CalculatedExpressionResult(0, List.of(), value);
    }

    @Override
    public CalculatedExpressionResult visit(TimestampValue value) {
        return new CalculatedExpressionResult(0, List.of(), value);
    }

}
