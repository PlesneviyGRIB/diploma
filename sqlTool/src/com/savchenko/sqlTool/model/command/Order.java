package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.structure.Column;

public record Order(Column column, boolean reverse) implements OrderSpecifier {}
