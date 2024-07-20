package com.core.sqlTool.model.command;

import com.core.sqlTool.model.command.domain.ComplexCalculedCommand;
import com.core.sqlTool.model.complexity.CalculatorEntry;
import com.core.sqlTool.model.domain.Column;
import com.core.sqlTool.model.domain.HeaderRow;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.domain.Projection;
import com.core.sqlTool.model.expression.BooleanValue;
import com.core.sqlTool.model.expression.Expression;
import com.core.sqlTool.model.resolver.Resolver;
import com.core.sqlTool.model.visitor.ContextSensitiveExpressionQualifier;
import com.core.sqlTool.model.visitor.ExpressionCalculator;
import com.core.sqlTool.model.visitor.ExpressionValidator;
import com.core.sqlTool.model.visitor.ValueInjector;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;


public record SelectCommand(List<Expression> expressions) implements ComplexCalculedCommand {

    @Override
    public LazyTable run(LazyTable lazyTable, Projection projection, Resolver resolver, CalculatorEntry calculatorEntry) {

        var expressionValidator = new ExpressionValidator(lazyTable.columns(), lazyTable.externalRow());
        var columns = getColumns(lazyTable.name(), expressions, expressionValidator);

        var calculatedValueOptionals = expressions.stream()
                .map(expression -> {

                    var isContextSensitiveExpression = expression.accept(new ContextSensitiveExpressionQualifier(lazyTable.columns()));

                    if (isContextSensitiveExpression) {
                        return Optional.empty();
                    }

                    return expression
                            .accept(new ValueInjector(HeaderRow.empty(), lazyTable.externalRow()))
                            .accept(new ExpressionCalculator(resolver, HeaderRow.empty(), lazyTable.externalRow()));
                })
                .toList();

//        Function<Row, Row> mapper = row -> expressions.stream()
//                .map()


//        var isContextSensitiveExpression = expression.accept(new ContextSensitiveExpressionQualifier(expressions));
//
//        var valueProvider = isContextSensitiveExpression ?
//                Optional.<BooleanValue>empty() :
//                Optional.of((BooleanValue) expression
//                        .accept(new ValueInjector(HeaderRow.empty(), externalRow))
//                        .accept(new ExpressionCalculator(resolver, HeaderRow.empty(), externalRow)));


        //return new LazyTable(lazyTable.name(), columns, lazyTable.dataStream().map(mapper), lazyTable.externalRow());
        return null;
    }

    private List<Column> getColumns(String tableName, List<Expression> expressions, ExpressionValidator expressionValidator) {

        var index = new AtomicInteger(0);

        return expressions.stream()
                .map((expression) -> {

                    if (expression instanceof Column column) {
                        return new Column(column.tableName(), column.columnName(), column.columnType());
                    }

                    var columnName = "column_%s".formatted(index.getAndIncrement());
                    var columnType = expression.accept(expressionValidator);

                    return new Column(tableName, columnName, columnType);
                })
                .toList();
    }

}
