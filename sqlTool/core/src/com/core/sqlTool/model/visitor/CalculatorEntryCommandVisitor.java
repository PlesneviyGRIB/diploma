package com.core.sqlTool.model.visitor;

import com.core.sqlTool.model.command.*;
import com.core.sqlTool.model.complexity.CalculatorEntry;
import com.core.sqlTool.model.expression.Expression;
import com.core.sqlTool.utils.PrinterUtils;
import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CalculatorEntryCommandVisitor implements Command.Visitor<String> {

    private static final Integer COMMAND_WIDTH = 10;

    private final CalculatorEntry calculatorEntry;

    @Override
    public String visit(ConstructIndexCommand command) {
        return "%s '%s'".formatted(formatCommand("Index"), command.index().getName());
    }

    @Override
    public String visit(DistinctCommand command) {
        return "%s".formatted(formatCommand("Distinct"));
    }

    @Override
    public String visit(FromCommand command) {
        return "%s '%s'".formatted(formatCommand("From"), command.tableName());
    }

    @Override
    public String visit(GroupByCommand command) {

        var expressions = command.expressions().stream()
                .map(Expression::stringify)
                .collect(Collectors.joining(", "));

        var aggregations = command.aggregations().stream()
                .map(pair -> "%s(%s)".formatted(pair.getRight().getClass().getSimpleName().toLowerCase(), pair.getLeft().stringify()))
                .collect(Collectors.joining(", "));

        return "%s %s, %s".formatted(formatCommand("Group by"), expressions, aggregations);
    }

    @Override
    public String visit(LimitCommand command) {
        return "%s %s".formatted(formatCommand("Limit"), command.limit());
    }

    @Override
    public String visit(OffsetCommand command) {
        return "%s %s".formatted(formatCommand("Offset"), command.offset());
    }

    @Override
    public String visit(OrderByCommand command) {

        var orders = command.orders().stream()
                .map(order -> "%s %s".formatted(order.getLeft().stringify(), order.getRight() ? "asc" : "desc"))
                .collect(Collectors.joining(", "));

        return "%s %s".formatted(formatCommand("Order by"), orders);
    }

    @Override
    public String visit(SelectCommand command) {

        var expressions = command.expressions().stream()
                .map(Expression::stringify)
                .collect(Collectors.joining(", "));

        return "%s %s".formatted(formatCommand("Select"), expressions);
    }

    @Override
    public String visit(TableAliasCommand command) {
        return "%s '%s'".formatted(formatCommand("as").toLowerCase(), command.alias());
    }

    @Override
    public String visit(WhereCommand command) {
        return "%s %s".formatted(formatCommand("Where"), command.expression().stringify());
    }

    @Override
    public String visit(JoinCommand command) {
        return "%s".formatted(formatCommand("Join"));
    }

    private String formatCommand(String commandName) {
        return PrinterUtils.fixedWidth(commandName, COMMAND_WIDTH);
    }

}
