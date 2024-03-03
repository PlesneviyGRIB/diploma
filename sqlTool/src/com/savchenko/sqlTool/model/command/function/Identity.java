package com.savchenko.sqlTool.model.command.function;

import com.savchenko.sqlTool.exception.UnexpectedException;
import com.savchenko.sqlTool.model.expression.ExpressionList;
import com.savchenko.sqlTool.model.expression.Value;

public class Identity implements AggregationFunction {
    @Override
    public Value<?> apply(ExpressionList list) {
        var expressions = list.expressions();
        if (expressions.size() != 1) {
            throw new UnexpectedException();
        }
        return expressions.get(0);
    }

}
