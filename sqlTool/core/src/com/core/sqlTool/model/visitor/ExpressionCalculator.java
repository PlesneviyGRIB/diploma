package com.core.sqlTool.model.visitor;

import com.core.sqlTool.exception.MoreThanOneColumnInSubQueryException;
import com.core.sqlTool.exception.UnexpectedExpressionException;
import com.core.sqlTool.model.domain.Column;
import com.core.sqlTool.model.domain.ExternalHeaderRow;
import com.core.sqlTool.model.domain.HeaderRow;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.expression.*;
import com.core.sqlTool.model.resolver.Resolver;
import com.core.sqlTool.utils.ModelUtils;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static com.client.sqlTool.expression.Operator.*;

@RequiredArgsConstructor
public class ExpressionCalculator implements Expression.Visitor<Value<?>> {

    private final Resolver resolver;

    private final HeaderRow headerRow;

    private final ExternalHeaderRow externalRow;

    @Override
    public Value<?> visit(ExpressionList list) {
        throw new UnexpectedExpressionException(list);
    }

    @Override
    public Value<?> visit(SubTable table) {
        throw new UnexpectedExpressionException(table);
    }

    @Override
    public Value<?> visit(Column column) {
        return headerRow.getValue(column)
                .or(() -> externalRow.getValue(column))
                .orElseThrow(() -> new UnexpectedExpressionException(column));
    }

    @Override
    public BooleanValue visit(UnaryOperation operation) {

        var specialResultOpt = handleSpecialUnaryCases(operation);
        if (specialResultOpt.isPresent()) {
            return specialResultOpt.get();
        }

        var operator = operation.operator();
        var value = operation.expression().accept(this);

        if (operator == IS_NOT_NULL) {
            return new BooleanValue(!(value instanceof NullValue));
        }
        if (operator == IS_NULL) {
            return new BooleanValue(value instanceof NullValue);
        }
        if (operator == NOT) {
            var prevValue = ((BooleanValue) value).value();
            return new BooleanValue(!prevValue);
        }

        throw new UnexpectedExpressionException(operation);
    }

    @Override
    public Value<?> visit(BinaryOperation operation) {

        var specialResult = handleSpecialBinaryCases(operation).orElse(null);
        if (Objects.nonNull(specialResult)) {
            return specialResult;
        }

        Supplier<Value<?>> leftSupplier = () -> operation.left().accept(this);
        Supplier<Value<?>> rightSupplier = () -> operation.right().accept(this);

        switch (operation.operator()) {
            case AND -> {
                return new BooleanValue(
                        BooleanValue.class.cast(leftSupplier.get()).value()
                                && BooleanValue.class.cast(rightSupplier.get()).value()
                );
            }
            case OR -> {
                return new BooleanValue(
                        BooleanValue.class.cast(leftSupplier.get()).value()
                                || BooleanValue.class.cast(rightSupplier.get()).value()
                );
            }
            case EQ -> {
                return new BooleanValue(((Value) leftSupplier.get()).compareTo(rightSupplier.get()) == 0);
            }
            case NOT_EQ -> {
                return new BooleanValue(((Value) leftSupplier.get()).compareTo(rightSupplier.get()) != 0);
            }
            case GREATER_OR_EQ -> {
                return new BooleanValue(((Value) leftSupplier.get()).compareTo(rightSupplier.get()) >= 0);
            }
            case LESS_OR_EQ -> {
                return new BooleanValue(((Value) leftSupplier.get()).compareTo(rightSupplier.get()) <= 0);
            }
            case GREATER -> {
                return new BooleanValue(((Value) leftSupplier.get()).compareTo(rightSupplier.get()) > 0);
            }
            case LESS -> {
                return new BooleanValue(((Value) leftSupplier.get()).compareTo(rightSupplier.get()) < 0);
            }
            case PLUS, MINUS, MULTIPLY, DIVISION, MOD -> {
                return ((Value) leftSupplier.get()).processArithmetic(operation.operator(), rightSupplier.get());
            }
            case LIKE -> {
                var target = ((StringValue) leftSupplier.get()).value();
                var pattern = ((StringValue) rightSupplier.get()).value();
                var matches = target.matches(ModelUtils.sqlPatternToJavaPattern(pattern));
                return new BooleanValue(matches);
            }
            default -> throw new UnexpectedExpressionException(operation);
        }
    }

    @Override
    public BooleanValue visit(TernaryOperation operation) {

        if (operation.operator() != BETWEEN) {
            throw new UnexpectedExpressionException(operation);
        }

        var value = operation.first().accept(this);

        return new BooleanValue(
                ((Value) operation.second().accept(this)).compareTo(value) <= 0 &&
                        ((Value) operation.third().accept(this)).compareTo(value) >= 0
        );
    }

    @Override
    public NullValue visit(NullValue value) {
        return value;
    }

    @Override
    public StringValue visit(StringValue value) {
        return value;
    }

    @Override
    public BooleanValue visit(BooleanValue value) {
        return value;
    }

    @Override
    public NumberValue visit(NumberValue value) {
        return value;
    }

    @Override
    public FloatNumberValue visit(FloatNumberValue value) {
        return value;
    }

    @Override
    public TimestampValue visit(TimestampValue value) {
        return value;
    }

    @Override
    public Value<?> visit(NamedExpression value) {
        return value.expression().accept(this);
    }

    private Optional<BooleanValue> handleSpecialUnaryCases(UnaryOperation operation) {

        if (operation.operator() == EXISTS) {

            if (operation.expression() instanceof SubTable subTable) {
                var table = resolver.resolve(subTable.commands(), getMergedExternalHeaderRow()).lazyTable();
                return Optional.of(new BooleanValue(table.dataStream().findAny().isPresent()));
            }

        }

        return Optional.empty();
    }

    private Optional<Value<?>> handleSpecialBinaryCases(BinaryOperation operation) {

        if (operation.operator() == IN) {

            if (operation.right() instanceof ExpressionList list) {
                return Optional.of(processInListOperation(operation.left().accept(this), list));
            }

            if (operation.right() instanceof SubTable subTable) {
                var table = resolver.resolve(subTable.commands(), getMergedExternalHeaderRow()).lazyTable();
                return Optional.of(processInTableOperation(operation.left().accept(this), table));
            }
        }

        return Optional.empty();
    }

    private BooleanValue processInTableOperation(Value<?> value, LazyTable lazyTable) {

        if (lazyTable.columns().size() != 1) {
            throw new MoreThanOneColumnInSubQueryException(lazyTable.columns());
        }

        var columnData = lazyTable.dataStream()
                .map(row -> row.values().get(0))
                .toList();

        return processInListOperation(value, new ExpressionList(columnData));
    }

    private BooleanValue processInListOperation(Value<?> value, ExpressionList list) {

        var presents = list.expressions().stream()
                .map(e -> e.accept(this))
                .anyMatch(val -> val.equals(value));

        return new BooleanValue(presents);
    }

    private ExternalHeaderRow getMergedExternalHeaderRow() {
        return externalRow.merge(new ExternalHeaderRow(headerRow.columns(), headerRow.row()));
    }

}
