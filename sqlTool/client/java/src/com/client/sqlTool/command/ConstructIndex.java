package com.client.sqlTool.command;

import com.client.sqlTool.domain.Column;

import java.util.List;

public record ConstructIndex(Index index, List<Column> columns) implements Command {
}
