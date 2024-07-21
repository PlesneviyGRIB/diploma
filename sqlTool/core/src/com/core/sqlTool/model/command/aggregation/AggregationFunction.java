package com.core.sqlTool.model.command.aggregation;

import com.core.sqlTool.model.expression.ValueList;
import com.core.sqlTool.model.expression.Value;

@FunctionalInterface
public interface AggregationFunction {

    Value<?> aggregate(ValueList list);

}
