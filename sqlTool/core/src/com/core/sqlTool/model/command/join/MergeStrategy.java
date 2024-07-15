package com.core.sqlTool.model.command.join;

import com.core.sqlTool.model.complexity.CalculatorEntry;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.expression.Expression;
import com.core.sqlTool.model.resolver.Resolver;
import com.core.sqlTool.support.JoinStreams;

public class MergeStrategy implements JoinStrategy {

    @Override
    public JoinStreams run(LazyTable lazyTable, LazyTable joinedLazyTable, Expression expression, Resolver resolver, CalculatorEntry calculatorEntry) {
        return new LoopStrategy().run(lazyTable, joinedLazyTable, expression, resolver, calculatorEntry);
    }

}
