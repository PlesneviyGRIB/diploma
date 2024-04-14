package com.savchenko.sqlTool.model.complexity;

import com.savchenko.sqlTool.model.command.domain.ComplexCalculedCommand;
import com.savchenko.sqlTool.model.visitor.ExpressionPrinter;
import com.savchenko.sqlTool.utils.printer.CalculatorPrinter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

import static java.lang.String.format;

public record ComplexCalculatorEntry(ComplexCalculedCommand command,
                                     CalculedExpressionResult calculedExpressionResult,
                                     Integer count,
                                     boolean isContextSensitive) implements CalculatorEntry, TotalCalculed {

    @Override
    public String stringify(String prefix) {
        var template =
                """
                        %s %s
                            Expression: %s
                            %s (expression complexity) %s %s (number of calculations) = %s (total)"""
                        .formatted(
                                stringifyCommand(command), getTotalComplexity(),
                                calculedExpressionResult.expression().accept(new ExpressionPrinter()),
                                calculedExpressionResult.complexity(), getSign(), count, getTotalComplexity()
                        );

        var text = Arrays.stream(template.split("\n"))
                .map(s -> toRow(prefix, "%s", s))
                .collect(Collectors.joining("\n"));

        var prefixRorSubTable = toRow(prefix, "%s", "    | ");
        var subTablesVerbose = calculedExpressionResult.calculators().stream()
                .map(c -> new CalculatorPrinter(c, prefixRorSubTable, true).stringify())
                .collect(Collectors.joining(format("\n%s\n", prefix)));

        if (StringUtils.isNoneBlank(subTablesVerbose)) {
            text += format("\n%s\n%s", prefix, subTablesVerbose);
        }

        return text;
    }

    @Override
    public Integer getTotalComplexity() {
        return isContextSensitive ?
                calculedExpressionResult.complexity() * count :
                calculedExpressionResult.complexity() + count;
    }

    private String getSign() {
        return isContextSensitive ? "*" : "+";
    }

}