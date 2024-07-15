package com.core.sqlTool.model.resolver;

import com.core.sqlTool.model.complexity.Calculator;
import com.core.sqlTool.model.domain.LazyTable;

public record ResolverResult(LazyTable lazyTable, Calculator calculator) { }
