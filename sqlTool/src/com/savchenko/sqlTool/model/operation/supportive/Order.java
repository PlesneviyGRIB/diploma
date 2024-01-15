package com.savchenko.sqlTool.model.operation.supportive;

import com.savchenko.sqlTool.model.Column;

public record Order(Column column, boolean reverse) {}
