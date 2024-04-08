package com.savchenko.sqlTool.model.complexity;

import com.savchenko.sqlTool.model.command.join.Join;
import com.savchenko.sqlTool.model.visitor.ExpressionPrinter;
import com.savchenko.sqlTool.utils.printer.CalculatorPrinter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

import static java.lang.String.format;

public record JoinCalculedEntry(Join command,
                                Calculator calculator,
                                Integer remainderSize,
                                CalculedExpressionResult calculedExpressionResult,
                                Integer count,
                                boolean isContextSensitive) implements CalculatorEntry {

    @Override
    public String stringify(String prefix) {
        var template =
                """
                        %s[%s] %s
                        %s
                            Expression: %s
                            %s (joined table complicity) + %s (expression complicity) %s %s (number of calculations) = %s (total)"""
                        .formatted(
                                stringifyCommand(command), command.getStrategy(), getTotalComplexityWithoutRemainder(),
                                new CalculatorPrinter(calculator, "    | ", false).stringify(),
                                calculedExpressionResult.expression().accept(new ExpressionPrinter()),
                                calculator.getTotalComplexity(), calculedExpressionResult.complexity(), getSign(), count, getTotalComplexityWithoutRemainder()
                        );

        var text = Arrays.stream(template.split("\n"))
                .map(s -> format("%s%s", prefix, s))
                .collect(Collectors.joining("\n"));

        var prefixRorSubTable = toRow(prefix,"%s", "    | ");
        var subTablesVerbose = calculedExpressionResult.calculators().stream()
                .map(c -> new CalculatorPrinter(c, prefixRorSubTable, true).stringify())
                .collect(Collectors.joining(format("\n%s\n", prefix)));

        if(StringUtils.isNoneBlank(subTablesVerbose)) {
            text += format("\n%s\n%s", prefix, subTablesVerbose);
        }

        return text;
    }

    @Override
    public Integer getTotalComplexity() {
        return calculator.getTotalComplexity() + remainderSize +
                (isContextSensitive ?
                        calculedExpressionResult.complexity() * count :
                        calculedExpressionResult.complexity() + count);
    }

    public Integer getTotalComplexityWithoutRemainder() {
        return calculator.getTotalComplexity() +
                (isContextSensitive ?
                        calculedExpressionResult.complexity() * count :
                        calculedExpressionResult.complexity() + count);
    }

    private String getSign() {
        return isContextSensitive ? "*" : "+";
    }

}
