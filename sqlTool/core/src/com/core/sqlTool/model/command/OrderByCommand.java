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
import org.apache.commons.lang3.tuple.Pair;

import java.util.Comparator;
import java.util.List;

public record OrderByCommand(List<Pair<Expression, Boolean>> orders) implements Command {

    @Override
    public LazyTable run(LazyTable lazyTable, Projection projection, Resolver resolver, CalculatorEntry calculatorEntry) {

        var expressions = orders.stream().map(Pair::getLeft).toList();
        var calculatedValueByExpressionMap = ExpressionUtils.calculateContextInsensitiveExpressions(expressions, lazyTable, resolver);

        Comparator<Row> rowsComparator = (row1, row2) -> orders.stream()
                .map(order -> {

                    var expression = order.getLeft();

                    var headerRow1 = new HeaderRow(lazyTable.columns(), row1);
                    var headerRow2 = new HeaderRow(lazyTable.columns(), row2);

                    var value1 = ExpressionUtils.calculateExpression(expression, headerRow1, lazyTable.externalRow(), resolver, calculatedValueByExpressionMap);
                    var value2 = ExpressionUtils.calculateExpression(expression, headerRow2, lazyTable.externalRow(), resolver, calculatedValueByExpressionMap);

                    var expressionResultTypeResolver = new ExpressionResultTypeResolver(lazyTable.columns(), lazyTable.externalRow());
                    var valueType = expression.accept(expressionResultTypeResolver);

                    var res = ModelUtils.compareValues(value1, value2, valueType);
                    if (!order.getRight()) {
                        res *= -1;
                    }
                    return res;
                })
                .filter(res -> res != 0)
                .findFirst().orElse(0);

        return new LazyTable(lazyTable.name(), lazyTable.columns(), lazyTable.dataStream().sorted(rowsComparator), lazyTable.externalRow());
    }

    @Override
    public  <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}
