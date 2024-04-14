package com.savchenko.sqlTool.model.resolver;

import com.savchenko.sqlTool.model.complexity.ExecutedCalculatorEntry;
import com.savchenko.sqlTool.model.domain.Table;

public record CommandResult(Table table, ExecutedCalculatorEntry calculatorEntry) {
}
