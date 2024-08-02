package com.core.sqlTool.model.visitor;

import com.core.sqlTool.exception.MoreThanOneColumnInSubQueryException;
import com.core.sqlTool.exception.UnexpectedExpressionException;
import com.core.sqlTool.model.complexity.CalculatedExpressionResult;
import com.core.sqlTool.model.domain.Column;
import com.core.sqlTool.model.domain.ExternalHeaderRow;
import com.core.sqlTool.model.domain.HeaderRow;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.expression.*;
import com.core.sqlTool.model.resolver.Resolver;
import com.core.sqlTool.utils.ModelUtils;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.client.sqlTool.expression.Operator.*;

@RequiredArgsConstructor
public class ExpressionCalculator implements Expression.Visitor<CalculatedExpressionResult> {

    private final Resolver resolver;

    private final HeaderRow headerRow;

    private final ExternalHeaderRow externalRow;

    @Override
    public CalculatedExpressionResult visit(ExpressionList list) {
        throw new UnexpectedExpressionException(list);
    }

    @Override
    public CalculatedExpressionResult visit(SubQuery table) {
        throw new UnexpectedExpressionException(table);
    }

    @Override
    public CalculatedExpressionResult visit(Column column) {

        var value = headerRow.getValue(column)
                .or(() -> externalRow.getValue(column))
                .orElseThrow(() -> new UnexpectedExpressionException(column));

        return CalculatedExpressionResult.eager(value, 1);
    }

    @Override
    public CalculatedExpressionResult visit(UnaryOperation operation) {

        var specialResultOpt = handleSpecialUnaryCases(operation);
        if (specialResultOpt.isPresent()) {
            return specialResultOpt.get();
        }

        var operator = operation.operator();
        var calculatedExpressionResult = operation.expression().accept(this);
        var value = calculatedExpressionResult.getValue();

        if (operator == IS_NOT_NULL) {
            return CalculatedExpressionResult.eager(new BooleanValue(!(value instanceof NullValue)), 1).merge(calculatedExpressionResult);
        }
        if (operator == IS_NULL) {
            return CalculatedExpressionResult.eager(new BooleanValue(value instanceof NullValue), 1).merge(calculatedExpressionResult);
        }
        if (operator == NOT) {
            var prevValue = ((BooleanValue) value).value();
            return CalculatedExpressionResult.eager(new BooleanValue(!prevValue), 1).merge(calculatedExpressionResult);
        }

        throw new UnexpectedExpressionException(operation);
    }

    @Override
    public CalculatedExpressionResult visit(BinaryOperation operation) {

        var specialResult = handleSpecialBinaryCases(operation).orElse(null);
        if (Objects.nonNull(specialResult)) {
            return specialResult;
        }

        var left = operation.left().accept(this);
        var right = CalculatedExpressionResult.lazy(() -> operation.right().accept(this));

        Function<Predicate<Integer>, CalculatedExpressionResult> comparison = predicate -> {
            var value = predicate.test(((Value) left.getValue()).compareTo(right.getValue()));
            return CalculatedExpressionResult.eager(new BooleanValue(value), 1).merge(left).merge(right);
        };

        switch (operation.operator()) {
            case AND -> {
                var value = BooleanValue.class.cast(left.getValue()).value() && BooleanValue.class.cast(right.getValue()).value();
                return CalculatedExpressionResult.eager(new BooleanValue(value), 1).merge(left).merge(right);
            }
            case OR -> {
                var value = BooleanValue.class.cast(left.getValue()).value() || BooleanValue.class.cast(right.getValue()).value();
                return CalculatedExpressionResult.eager(new BooleanValue(value), 1).merge(left).merge(right);
            }
            case EQ -> {
                return comparison.apply(comparisonResult -> comparisonResult == 0);
            }
            case NOT_EQ -> {
                return comparison.apply(comparisonResult -> comparisonResult != 0);
            }
            case GREATER_OR_EQ -> {
                return comparison.apply(comparisonResult -> comparisonResult >= 0);
            }
            case LESS_OR_EQ -> {
                return comparison.apply(comparisonResult -> comparisonResult <= 0);
            }
            case GREATER -> {
                return comparison.apply(comparisonResult -> comparisonResult > 0);
            }
            case LESS -> {
                return comparison.apply(comparisonResult -> comparisonResult < 0);
            }
            case PLUS, MINUS, MULTIPLY, DIVISION, MOD -> {
                var value = ((Value) left.getValue()).processArithmetic(operation.operator(), right.getValue());
                return CalculatedExpressionResult.eager(value, 1).merge(left).merge(right);
            }
            case LIKE -> {
                var target = ((StringValue) left.getValue()).value();
                var pattern = ((StringValue) right.getValue()).value();
                var matches = target.matches(ModelUtils.sqlPatternToJavaPattern(pattern));
                var complexity = target.length();

                return CalculatedExpressionResult.eager(new BooleanValue(matches), complexity).merge(left).merge(right);
            }
            default -> throw new UnexpectedExpressionException(operation);
        }
    }

    @Override
    public CalculatedExpressionResult visit(TernaryOperation operation) {

        if (operation.operator() != BETWEEN) {
            throw new UnexpectedExpressionException(operation);
        }

        var first = operation.first().accept(this);
        var second = CalculatedExpressionResult.lazy(() -> operation.second().accept(this));
        var third = CalculatedExpressionResult.lazy(() -> operation.third().accept(this));

        var value = ((Value) second.getValue()).compareTo(first.getValue()) <= 0 && ((Value) third.getValue()).compareTo(first.getValue()) >= 0;

        return CalculatedExpressionResult.eager(new BooleanValue(value), 2).merge(first).merge(second).merge(third);
    }

    @Override
    public CalculatedExpressionResult visit(NullValue value) {
        return CalculatedExpressionResult.eager(value, 0);
    }

    @Override
    public CalculatedExpressionResult visit(StringValue value) {
        return CalculatedExpressionResult.eager(value, 0);
    }

    @Override
    public CalculatedExpressionResult visit(BooleanValue value) {
        return CalculatedExpressionResult.eager(value, 0);
    }

    @Override
    public CalculatedExpressionResult visit(NumberValue value) {
        return CalculatedExpressionResult.eager(value, 0);
    }

    @Override
    public CalculatedExpressionResult visit(FloatNumberValue value) {
        return CalculatedExpressionResult.eager(value, 0);
    }

    @Override
    public CalculatedExpressionResult visit(TimestampValue value) {
        return CalculatedExpressionResult.eager(value, 0);
    }

    @Override
    public CalculatedExpressionResult visit(NamedExpression value) {
        return value.expression().accept(this);
    }

    private Optional<CalculatedExpressionResult> handleSpecialUnaryCases(UnaryOperation operation) {

        if (operation.operator() == EXISTS) {

            if (operation.expression() instanceof SubQuery subQuery) {

                var resolverResult = resolver.resolve(subQuery.commands(), getMergedExternalHeaderRow());
                var table = resolverResult.lazyTable();
                var tableComplexity = resolverResult.calculator().getTotalComplexity();
                var value = table.dataStream().findAny().isPresent();

                return Optional.of(CalculatedExpressionResult.eager(new BooleanValue(value), 1 + tableComplexity));
            }

        }

        return Optional.empty();
    }

    private Optional<CalculatedExpressionResult> handleSpecialBinaryCases(BinaryOperation operation) {

        if (operation.operator() == IN) {

            var calculatedExpressionResult = operation.left().accept(this);

            if (operation.right() instanceof ExpressionList list) {
                return Optional.of(processInStreamOperation(calculatedExpressionResult.getValue(), list.expressions().stream()).merge(calculatedExpressionResult));
            }

            if (operation.right() instanceof SubQuery subQuery) {

                var resolverResult = resolver.resolve(subQuery.commands(), getMergedExternalHeaderRow());
                var table = resolverResult.lazyTable();
                var tableComplexity = resolverResult.calculator().getTotalComplexity();

                return Optional.of(processInTableOperation(calculatedExpressionResult.getValue(), table).merge(CalculatedExpressionResult.eager(null, tableComplexity)).merge(calculatedExpressionResult));
            }
        }

        return Optional.empty();
    }

    private CalculatedExpressionResult processInTableOperation(Value<?> value, LazyTable lazyTable) {

        if (lazyTable.columns().size() != 1) {
            throw new MoreThanOneColumnInSubQueryException(lazyTable.columns());
        }

        return processInStreamOperation(value, lazyTable.dataStream().map(row -> row.values().get(0)));
    }

    private CalculatedExpressionResult processInStreamOperation(Value<?> value, Stream<Expression> expressionStream) {

        var usedCalculatedExpressionResults = new LinkedList<CalculatedExpressionResult>();

        var presents = expressionStream
                .map(expression -> expression.accept(this))
                .anyMatch(expressionResult -> {

                    var eqOperation = new BinaryOperation(EQ, value, expressionResult.getValue());
                    var calculatedExpressionResult = eqOperation.accept(this);

                    usedCalculatedExpressionResults.add(calculatedExpressionResult.merge(expressionResult));

                    return ((BooleanValue) calculatedExpressionResult.getValue()).value();
                });

        var calculatedExpressionResult = usedCalculatedExpressionResults.stream()
                .reduce(CalculatedExpressionResult.eager(null, 0), CalculatedExpressionResult::merge);

        return CalculatedExpressionResult.eager(new BooleanValue(presents), 0).merge(calculatedExpressionResult);
    }

    private ExternalHeaderRow getMergedExternalHeaderRow() {
        return externalRow.merge(new ExternalHeaderRow(headerRow.columns(), headerRow.row()));
    }

}
