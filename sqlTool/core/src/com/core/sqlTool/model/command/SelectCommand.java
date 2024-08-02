package com.core.sqlTool.model.command;

import com.core.sqlTool.model.complexity.CalculatorEntry;
import com.core.sqlTool.model.domain.HeaderRow;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.domain.Projection;
import com.core.sqlTool.model.domain.Row;
import com.core.sqlTool.model.expression.Expression;
import com.core.sqlTool.model.resolver.Resolver;
import com.core.sqlTool.model.visitor.ExpressionResultTypeResolver;
import com.core.sqlTool.utils.ExpressionUtils;
import com.core.sqlTool.utils.ModelUtils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


public record SelectCommand(List<Expression> expressions) implements Command {

    @Override
    public LazyTable run(LazyTable lazyTable, Projection projection, Resolver resolver, CalculatorEntry calculatorEntry) {

        var expressionResultTypeResolver = new ExpressionResultTypeResolver(lazyTable.columns(), lazyTable.externalRow());
        var columns = expressions.stream()
                .map((expression) -> ModelUtils.getColumnFromExpression(expression, lazyTable, expressionResultTypeResolver))
                .toList();

        var calculatedValueByExpressionMap = ExpressionUtils.calculateContextInsensitiveExpressions(expressions, lazyTable, resolver);

        Function<Row, Row> mapper = row -> {

            var headerRow = new HeaderRow(lazyTable.columns(), row);
            var externalRow = lazyTable.externalRow();

            return expressions.stream()
                    .map(expression -> ExpressionUtils.calculateExpression(expression, headerRow, externalRow, resolver, calculatedValueByExpressionMap))
                    .collect(Collectors.collectingAndThen(Collectors.toList(), Row::new));
        };

        return new LazyTable(lazyTable.name(), columns, lazyTable.dataStream().map(mapper), lazyTable.externalRow());
    }

    @Override
    public  <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}
