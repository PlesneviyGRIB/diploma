package com.savchenko.sqlTool.model.resolver;

import com.savchenko.sqlTool.model.complexity.ExecutedCalculatorEntry;
import com.savchenko.sqlTool.model.domain.LazyTable;

public record CommandResult(LazyTable lazyTable, ExecutedCalculatorEntry calculatorEntry) {
}
