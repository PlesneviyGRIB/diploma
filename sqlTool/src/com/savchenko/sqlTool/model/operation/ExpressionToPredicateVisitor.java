package com.savchenko.sqlTool.model.operation;

import com.savchenko.sqlTool.model.operator.ArithmeticOperator;
import com.savchenko.sqlTool.model.operator.EqOperator;
import com.savchenko.sqlTool.model.operator.LogicOperator;
import com.savchenko.sqlTool.model.operator.Operator;
import com.savchenko.sqlTool.model.predicate.Predicate;
import com.savchenko.sqlTool.model.structure.Column;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.lang.String.format;

public class ExpressionToPredicateVisitor implements Expression.Visitor<Predicate> {
    private final Function<Comparable<?>, Boolean> getBoolean = value -> {
        if(!(value instanceof Boolean)) {
            throw new RuntimeException(format("Expected value of BOOLEAN type but got other. Value is '%s'", value));
        }
        return (Boolean) value;
    };

    @Override
    public Predicate visit(Comparable<?> value) {
        var val = getBoolean.apply(value);
        return (columns, row) -> val;
    }
    @Override
    public Predicate visit(Column column) {
        return (columns, row) -> {
            var index = columns.indexOf(column);
            return getBoolean.apply(row.get(index));
        };
    }

    @Override
    public Predicate visit(UnaryOperation operation) {
        var op = operation.operator();
        switch (op) {
            case EXISTS -> {
                return (columns, row) -> {
                    var index = columns.indexOf(operation.column());

                    return Objects.nonNull(row.get(8));
                };
            }
            case IS_NULL -> {
                return (columns, row) -> {
                    var index = columns.indexOf(operation.column());
                    return Objects.isNull(row.get(index));
                };
            }
            default -> throw new RuntimeException(format("Unexpected operator '%s'", op));
        }
    }

    @Override
    public Predicate visit(BinaryOperation operation) {
        BiFunction<Expression, Predicate, Predicate> getPredicate = (exp, predicate) -> {
            if(exp instanceof BinaryOperation || exp instanceof UnaryOperation) {
                return exp.accept(ExpressionToPredicateVisitor.this);
            }
            return predicate;
        };

        var left = getPredicate.apply(operation.left(), Predicate.TRUE);
        var right = getPredicate.apply(operation.right(), Predicate.TRUE);

        return operation.operator().accept(new Operator.Visitor<>() {
            @Override
            public Predicate visit(EqOperator operator) {
                switch (operator) {
                    default -> throw new RuntimeException(format("Unexpected operator '%s'", operator));
                }
            }

            @Override
            public Predicate visit(LogicOperator operator) {
                switch (operator) {
                    case AND -> { return (columns, row) -> left.test(columns, row) && right.test(columns, row); }
                    case OR -> { return (columns, row) -> left.test(columns, row) || right.test(columns, row); }
                    default -> throw new RuntimeException(format("Unexpected operator '%s'", operator));
                }
            }

            @Override
            public Predicate visit(ArithmeticOperator operator) {
                switch (operator) {
                    default -> throw new RuntimeException(format("Unexpected operator '%s'", operator));
                }
            }
        });
    }
}
