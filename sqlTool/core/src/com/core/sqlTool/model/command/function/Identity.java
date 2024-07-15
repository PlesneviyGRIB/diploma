package com.core.sqlTool.model.command.function;

import com.core.sqlTool.exception.UnexpectedException;
import com.core.sqlTool.model.expression.ExpressionList;
import com.core.sqlTool.model.expression.Value;

import java.util.Objects;

public class Identity implements AggregationFunction {
    @Override
    public Value<?> apply(ExpressionList list) {
        var expressions = list.expressions();
        if (expressions.size() != 1) {
            throw new UnexpectedException();
        }
        return expressions.get(0);
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
