package com.savchenko.sqlTool.model.command.function;

import com.savchenko.sqlTool.model.expression.ExpressionList;
import com.savchenko.sqlTool.model.expression.Value;

import java.util.Objects;

public class Sum implements AggregationFunction {
    @Override
    public Value<?> apply(ExpressionList list) {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || !(o == null || getClass() != o.getClass());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(null);
    }

}
