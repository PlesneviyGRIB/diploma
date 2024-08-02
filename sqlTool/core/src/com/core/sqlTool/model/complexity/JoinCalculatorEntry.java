package com.core.sqlTool.model.complexity;

import com.core.sqlTool.model.command.join.JoinCommand;
import com.core.sqlTool.utils.printer.CalculatorPrinter;
import com.core.sqlTool.model.visitor.ExpressionPrinter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class JoinCalculatorEntry extends ComplexCalculatorEntry {

    private final Calculator calculator;

    public JoinCalculatorEntry(JoinCommand command, Calculator calculator, CalculatedExpressionResult calculatedExpressionResult, boolean isContextSensitive) {
        super(command, calculatedExpressionResult, isContextSensitive);
        this.calculator = calculator;
    }

    @Override
    public String stringify(String prefix) {
//        var template =
//                """
//                        %s[%s] %s
//                        %s
//                            Expression: %s
//                            %s (joined lazyTable complexity) + %s (expression complexity) %s %s (number of calculations) = %s (total)"""
//                        .formatted(
//                                stringifyCommand(), ((JoinCommand) command).getStrategy(), getTotalComplexity(),
//                                new CalculatorPrinter(calculator, "    | ", CalculatorPrinter.TableType.JOIN).stringify(),
//                                calculatedExpressionResult.expression().accept(new ExpressionPrinter()),
//                                calculator.getTotalComplexity(), calculatedExpressionResult.complexity(), getSign(), counter.get(), super.getTotalComplexity()
//                        );
//
//        var text = Arrays.stream(template.split("\n"))
//                .map(s -> format("%s%s", prefix, s))
//                .collect(Collectors.joining("\n"));
//
//        var prefixRorSubTable = toRow(prefix, "%s", "    | ");
//        var subTablesVerbose = calculatedExpressionResult.calculators().stream()
//                .map(c -> new CalculatorPrinter(c, prefixRorSubTable, CalculatorPrinter.TableType.INNER).stringify())
//                .collect(Collectors.joining(format("\n%s\n", prefix)));
//
//        if (StringUtils.isNoneBlank(subTablesVerbose)) {
//            text += format("\n%s\n%s", prefix, subTablesVerbose);
//        }

        return "text";
    }

    @Override
    public Integer getTotalComplexity() {
//        return calculator.getTotalComplexity() +
//                (isContextSensitive ?
//                        calculatedExpressionResult.complexity() * counter.get() :
//                        calculatedExpressionResult.complexity() + counter.get());
        return null;
    }

    @Override
    public Integer getFullComplexity() {
//        var expressionComplexity = calculatedExpressionResult.calculators().stream()
//                .map(c -> c.getFullComplexity() - c.getTotalComplexity())
//                .reduce(0, Integer::sum) + calculatedExpressionResult.complexity();
//
//        return calculator.getTotalComplexity() +
//                (isContextSensitive ?
//                        expressionComplexity * counter.get() :
//                        expressionComplexity + counter.get());r
        return null;
    }

}
