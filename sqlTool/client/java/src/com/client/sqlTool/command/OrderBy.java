package com.client.sqlTool.command;

import java.util.List;

public record OrderBy(List<Order> orders) implements Command {
}
