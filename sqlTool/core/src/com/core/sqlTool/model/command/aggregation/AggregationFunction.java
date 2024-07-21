package com.core.sqlTool.model.command.aggregation;

import com.core.sqlTool.model.expression.ExpressionList;
import com.core.sqlTool.model.expression.Value;

@FunctionalInterface
public interface AggregationFunction {

    Value<?> aggregate(ExpressionList list);

}
