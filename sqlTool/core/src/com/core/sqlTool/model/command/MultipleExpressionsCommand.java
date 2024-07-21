package com.core.sqlTool.model.command;

import com.core.sqlTool.model.complexity.CalculatorEntry;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.domain.Projection;
import com.core.sqlTool.model.expression.Expression;
import com.core.sqlTool.model.resolver.Resolver;

import java.util.List;

public interface MultipleExpressionsCommand extends Command {

    LazyTable run(LazyTable lazyTable, Projection projection, Resolver resolver, CalculatorEntry calculatorEntry);

    List<Expression> getExpressions();

    @Override
    default <T> T accept(Command.Visitor<T> visitor) {
        return visitor.visit(this);
    }

}
