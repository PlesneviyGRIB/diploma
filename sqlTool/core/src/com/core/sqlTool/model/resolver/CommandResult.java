package com.core.sqlTool.model.resolver;

import com.core.sqlTool.model.complexity.ExecutedCalculatorEntry;
import com.core.sqlTool.model.domain.LazyTable;

public record CommandResult(LazyTable lazyTable, ExecutedCalculatorEntry calculatorEntry) {
}
