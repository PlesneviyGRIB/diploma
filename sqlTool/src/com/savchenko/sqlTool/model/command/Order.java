package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.domain.Column;

public record Order(Column column, boolean reverse)  {}
