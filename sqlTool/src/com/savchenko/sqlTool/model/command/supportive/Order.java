package com.savchenko.sqlTool.model.command.supportive;

import com.savchenko.sqlTool.model.Column;

public record Order(Column column, boolean reverse) {}
