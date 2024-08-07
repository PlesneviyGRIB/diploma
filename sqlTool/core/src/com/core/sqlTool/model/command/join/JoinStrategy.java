package com.core.sqlTool.model.command.join;

import com.core.sqlTool.model.complexity.CalculatorEntry;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.expression.Expression;
import com.core.sqlTool.model.resolver.Resolver;
import com.core.sqlTool.support.JoinStreams;

public interface JoinStrategy {

    JoinStreams run(LazyTable lazyTable, LazyTable joinedLazyTable, Expression expression, Resolver resolver, CalculatorEntry calculatorEntry);

}
