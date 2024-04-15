package com.savchenko.sqlTool.model.complexity;

import com.savchenko.sqlTool.model.command.join.Join;
import com.savchenko.sqlTool.model.visitor.ExpressionPrinter;
import com.savchenko.sqlTool.utils.printer.CalculatorPrinter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.savchenko.sqlTool.utils.printer.CalculatorPrinter.TableType.INNER;
import static com.savchenko.sqlTool.utils.printer.CalculatorPrinter.TableType.JOIN;
import static java.lang.String.format;

public class JoinCalculatorEntry extends ExecutedCalculatorEntry {

    private final Calculator calculator;

    private final Integer remainderSize;

    private final CalculatedExpressionResult calculatedExpressionResult;

    private final Integer count;

    private final boolean isContextSensitive;

    public JoinCalculatorEntry(Join command, Calculator calculator, Integer remainderSize, CalculatedExpressionResult calculatedExpressionResult, Integer count, boolean isContextSensitive) {
        super(command);
        this.calculator = calculator;
        this.remainderSize = remainderSize;
        this.calculatedExpressionResult = calculatedExpressionResult;
        this.count = count;
        this.isContextSensitive = isContextSensitive;
    }

    @Override
    public String stringify(String prefix) {
        var template =
                """
                        %s[%s] %s
                        %s
                            Expression: %s
                            %s (joined table complexity) + %s (expression complexity) %s %s (number of calculations) = %s (total)"""
                        .formatted(
                                stringifyCommand(), ((Join) command).getStrategy(), getTotalComplexity(),
                                new CalculatorPrinter(calculator, "    | ", JOIN).stringify(),
                                calculatedExpressionResult.expression().accept(new ExpressionPrinter()),
                                calculator.getTotalComplexity(), calculatedExpressionResult.complexity(), getSign(), count, getTotalComplexityWithoutRemainder()
                        );

        var text = Arrays.stream(template.split("\n"))
                .map(s -> format("%s%s", prefix, s))
                .collect(Collectors.joining("\n"));

        var prefixRorSubTable = toRow(prefix, "%s", "    | ");
        var subTablesVerbose = calculatedExpressionResult.calculators().stream()
                .map(c -> new CalculatorPrinter(c, prefixRorSubTable, INNER).stringify())
                .collect(Collectors.joining(format("\n%s\n", prefix)));

        if (StringUtils.isNoneBlank(subTablesVerbose)) {
            text += format("\n%s\n%s", prefix, subTablesVerbose);
        }

        return text;
    }

    @Override
    public Integer getTotalComplexity() {
        return calculator.getTotalComplexity() + remainderSize +
                (isContextSensitive ?
                        calculatedExpressionResult.complexity() * count :
                        calculatedExpressionResult.complexity() + count);
    }

    @Override
    public Integer getFullComplexity() {
        var expressionComplexity = calculatedExpressionResult.calculators().stream()
                .map(c -> c.getFullComplexity() - c.getTotalComplexity())
                .reduce(0, Integer::sum) + getTotalComplexity();

        return isContextSensitive ?
                expressionComplexity * count :
                expressionComplexity + count;
    }

    public Integer getTotalComplexityWithoutRemainder() {
        return calculator.getTotalComplexity() +
                (isContextSensitive ?
                        calculatedExpressionResult.complexity() * count :
                        calculatedExpressionResult.complexity() + count);
    }

    private String getSign() {
        return isContextSensitive ? "*" : "+";
    }

}
