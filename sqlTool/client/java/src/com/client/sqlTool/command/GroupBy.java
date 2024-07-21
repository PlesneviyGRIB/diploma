package com.client.sqlTool.command;

import com.client.sqlTool.expression.Expression;

import java.util.List;

public record GroupBy(List<Expression> expressions, List<Aggregation> aggregations) implements Command {
}
