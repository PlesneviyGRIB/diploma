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
                                CalculedExpressionEntry calculedExpressionEntry,
                                Integer count,
                                boolean isContextSensitive) implements CalculatorEntry {

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String stringify(String prefix) {
        var entry = calculedExpressionEntry;
        var strings =
                """
                        %s[%s] %s
                        %s
                            Expression: %s
                            %s (joined table complicity) + %s (expression complicity) %s %s (number of calculations) = %s (total)""".formatted(
                        stringifyType(command), command.getStrategy(), (isContextSensitive ? entry.complexity() * count : entry.complexity() + count) + calculator.getTotalComplexity(),
                        new CalculatorPrinter(calculator, "    | ", false).stringify(), entry.expression().accept(new ExpressionPrinter()), calculator.getTotalComplexity(),
                        entry.complexity(), isContextSensitive ? "*" : "+", count,
                        (isContextSensitive ? entry.complexity() * count : entry.complexity() + count) + calculator.getTotalComplexity()
                ).split("\n");

        var res = Arrays.stream(strings).map(s -> format("%s%s", prefix, s)).collect(Collectors.joining("\n"));

        var subTablesVerbose = entry.calculators().stream()
                .map(c -> new CalculatorPrinter(c, format("%s%s", prefix, "    | "), true).stringify())
                .collect(Collectors.joining(format("\n%s\n", prefix)));

        if (StringUtils.isNoneBlank(subTablesVerbose)) {
            res += format("\n\n%s", subTablesVerbose);
        }

        return res;
    }

}
