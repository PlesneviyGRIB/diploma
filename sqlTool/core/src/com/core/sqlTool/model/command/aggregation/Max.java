package com.core.sqlTool.model.command.aggregation;

import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.domain.Row;
import com.core.sqlTool.model.expression.Value;
import com.core.sqlTool.model.expression.ExpressionList;
import com.core.sqlTool.model.resolver.Resolver;

import java.util.List;

public record Max() implements AggregationFunction {

    @Override
    public Value<?> aggregate(List<Value<?>> values) {
        var sortedValues = values.stream().sorted().toList();
        return sortedValues.get(sortedValues.size() - 1);
    }

}
