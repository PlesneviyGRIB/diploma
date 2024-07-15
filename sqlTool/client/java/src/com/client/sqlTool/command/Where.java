package com.client.sqlTool.command;

import com.client.sqlTool.expression.Expression;

public record Where(Expression expression) implements Command {
}
