package com.client.sqlTool.command;

import com.client.sqlTool.domain.JoinType;
import com.client.sqlTool.expression.Expression;
import com.client.sqlTool.domain.JoinStrategy;

import java.util.List;

public record Join(JoinType type, List<Command> commands, Expression expression, JoinStrategy strategy) implements Command {
}
