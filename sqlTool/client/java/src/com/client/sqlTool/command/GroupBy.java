package com.client.sqlTool.command;

import java.util.List;

public record GroupBy(List<Group> groups) implements Command {
}
