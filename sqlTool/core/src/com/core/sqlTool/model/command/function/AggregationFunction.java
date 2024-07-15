package com.core.sqlTool.model.command.function;

import com.core.sqlTool.model.expression.ExpressionList;
import com.core.sqlTool.model.expression.Value;

@FunctionalInterface
public interface AggregationFunction {

    Value<?> apply(ExpressionList list);

}
