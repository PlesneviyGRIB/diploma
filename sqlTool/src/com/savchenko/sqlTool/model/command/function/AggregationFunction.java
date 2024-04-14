package com.savchenko.sqlTool.model.command.function;

import com.savchenko.sqlTool.model.expression.ExpressionList;
import com.savchenko.sqlTool.model.expression.Value;

@FunctionalInterface
public interface AggregationFunction {

    Value<?> apply(ExpressionList list);

}
