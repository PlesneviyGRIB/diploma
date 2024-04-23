package com.savchenko.sqlTool.model.resolver;

import com.savchenko.sqlTool.model.complexity.Calculator;
import com.savchenko.sqlTool.model.domain.LazyTable;

public record ResolverResult(LazyTable lazyTable, Calculator calculator) { }
