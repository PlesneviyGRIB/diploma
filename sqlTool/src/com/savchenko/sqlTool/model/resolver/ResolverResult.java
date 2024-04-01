package com.savchenko.sqlTool.model.resolver;

import com.savchenko.sqlTool.model.complexity.Calculator;
import com.savchenko.sqlTool.model.domain.Table;

public record ResolverResult(Table table, Calculator calculator) { }
