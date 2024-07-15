package com.core.sqlTool.model.complexity;

import com.core.sqlTool.model.command.domain.ComplexCalculedCommand;
import com.core.sqlTool.utils.printer.CalculatorPrinter;
import com.core.sqlTool.model.visitor.ExpressionPrinter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class ComplexCalculatorEntry extends ExecutedCalculatorEntry implements TotalCalculated {

    protected final CalculatedExpressionResult calculatedExpressionResult;

    protected final boolean isContextSensitive;

    public ComplexCalculatorEntry(ComplexCalculedCommand command, CalculatedExpressionResult calculatedExpressionResult, boolean isContextSensitive) {
        super(command);
        this.calculatedExpressionResult = calculatedExpressionResult;
        this.isContextSensitive = isContextSensitive;
    }

    @Override
    public String stringify(String prefix) {
        var template =
                """
                        %s %s
                            Expression: %s
                            %s (expression complexity) %s %s (number of calculations) = %s (total)"""
                        .formatted(
                                stringifyCommand(), getTotalComplexity(),
                                calculatedExpressionResult.expression().accept(new ExpressionPrinter()),
                                calculatedExpressionResult.complexity(), getSign(), counter.get(), getTotalComplexity()
                        );

        var text = Arrays.stream(template.split("\n"))
                .map(s -> toRow(prefix, "%s", s))
                .collect(Collectors.joining("\n"));

        var prefixRorSubTable = toRow(prefix, "%s", "    | ");
        var subTablesVerbose = calculatedExpressionResult.calculators().stream()
                .map(c -> new CalculatorPrinter(c, prefixRorSubTable, CalculatorPrinter.TableType.INNER).stringify())
                .collect(Collectors.joining(format("\n%s\n", prefix)));

        if (StringUtils.isNoneBlank(subTablesVerbose)) {
            text += format("\n%s\n%s", prefix, subTablesVerbose);
        }

        return text;
    }

    @Override
    public Integer getTotalComplexity() {
        return isContextSensitive ?
                calculatedExpressionResult.complexity() * counter.get() :
                calculatedExpressionResult.complexity() + counter.get();
    }

    @Override
    public Integer getFullComplexity() {
        var expressionComplexity = calculatedExpressionResult.calculators().stream()
                .map(c -> c.getFullComplexity() - c.getTotalComplexity())
                .reduce(0, Integer::sum) + calculatedExpressionResult.complexity();

        return isContextSensitive ?
                expressionComplexity * counter.get() :
                expressionComplexity + counter.get();
    }

    protected String getSign() {
        return isContextSensitive ? "*" : "+";
    }

}