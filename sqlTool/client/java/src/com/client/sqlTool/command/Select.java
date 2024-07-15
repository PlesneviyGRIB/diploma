package com.client.sqlTool.command;

import com.client.sqlTool.expression.Expression;

import java.util.List;

public record Select(List<Expression> selectables) implements Command {
}
