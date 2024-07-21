package com.core.sqlTool.model.command.aggregation;

import com.core.sqlTool.model.expression.Value;

import java.util.List;

public sealed interface AggregationFunction permits Average, Count, Max, Min, Sum {

    Value<?> aggregate(List<Value<?>> values);


}
