package com.savchenko.sqlTool.model.visitor;

import com.savchenko.sqlTool.exception.UnexpectedException;
import com.savchenko.sqlTool.exception.UnexpectedExpressionException;
import com.savchenko.sqlTool.model.resolver.Resolver;
import com.savchenko.sqlTool.model.domain.Column;
import com.savchenko.sqlTool.model.domain.ExternalRow;
import com.savchenko.sqlTool.model.domain.Table;
import com.savchenko.sqlTool.model.expression.*;
import com.savchenko.sqlTool.utils.ModelUtils;

import java.util.Objects;
import java.util.Optional;

import static com.savchenko.sqlTool.model.operator.Operator.*;

public class ExpressionCalculator implements Expression.Visitor<Value<?>> {

    private final Resolver resolver;

    private final ExternalRow externalRow;

    public ExpressionCalculator(Resolver resolver, ExternalRow externalRow) {
        this.resolver = resolver;
        this.externalRow = externalRow;
    }

    @Override
    public Value<?> visit(ExpressionList list) {
        throw new UnexpectedExpressionException(list);
    }

    @Override
    public Value<?> visit(SubTable table) {
        throw new UnexpectedExpressionException(table);
    }

    @Override
    public Value<Column> visit(Column column) {
        throw new UnexpectedExpressionException(column);
    }

    @Override
    public Value<BooleanValue> visit(UnaryOperation operation) {

        var specialResult = handleSpecialUnaryCases(operation).orElse(null);
        if (Objects.nonNull(specialResult)) {
            return specialResult;
        }

        var op = operation.operator();
        var value = operation.expression().accept(this);
        if (op == EXISTS) {
            return new BooleanValue(!(value instanceof NullValue));
        }
        if (op == IS_NULL) {
            return new BooleanValue(value instanceof NullValue);
        }
        if (op == NOT) {
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

        var left = operation.left().accept(this);
        var right = operation.right().accept(this);
        var targetClass = left.getClass();
        var l = targetClass.cast(left);
        var r = targetClass.cast(right);

        switch (operation.operator()) {
            case AND -> {
                var val1 = BooleanValue.class.cast(left);
                var val2 = BooleanValue.class.cast(right);
                return new BooleanValue(val1.value() && val2.value());
            }
            case OR -> {
                var val1 = BooleanValue.class.cast(left);
                var val2 = BooleanValue.class.cast(right);
                return new BooleanValue(val1.value() || val2.value());
            }
            case EQ -> {
                return new BooleanValue(l.compareTo(r) == 0);
            }
            case NOT_EQ -> {
                return new BooleanValue(l.compareTo(r) != 0);
            }
            case GREATER_OR_EQ -> {
                return new BooleanValue(l.compareTo(r) >= 0);
            }
            case LESS_OR_EQ -> {
                return new BooleanValue(l.compareTo(r) <= 0);
            }
            case GREATER -> {
                return new BooleanValue(l.compareTo(r) > 0);
            }
            case LESS -> {
                return new BooleanValue(l.compareTo(r) < 0);
            }
            case PLUS, MINUS, MULTIPLY, DIVISION, MOD -> {
                return l.processArithmetic(operation.operator(), r);
            }
            case LIKE -> {
                var target = ((StringValue) l).value();
                var pattern = ((StringValue) r).value();
                var matches = target.matches(ModelUtils.sqlPatternToJavaPattern(pattern));
                return new BooleanValue(matches);
            }
            default -> throw new UnexpectedExpressionException(operation);
        }
    }

    @Override
    public Value<BooleanValue> visit(TernaryOperation operation) {
        var f = operation.first().accept(this);
        var targetClass = f.getClass();
        var first = targetClass.cast(f);
        var second = targetClass.cast(operation.second().accept(this));
        var third = targetClass.cast(operation.third().accept(this));
        return new BooleanValue(second.compareTo(first) <= 0 && first.compareTo(third) < 0);
    }

    @Override
    public Value<Object> visit(NullValue value) {
        return value;
    }

    @Override
    public Value<StringValue> visit(StringValue value) {
        return value;
    }

    @Override
    public Value<BooleanValue> visit(BooleanValue value) {
        return value;
    }

    @Override
    public Value<IntegerNumber> visit(IntegerNumber value) {
        return value;
    }

    @Override
    public Value<LongNumber> visit(LongNumber value) {
        return value;
    }

    @Override
    public Value<FloatNumber> visit(FloatNumber value) {
        return value;
    }

    @Override
    public Value<DoubleNumber> visit(DoubleNumber value) {
        return value;
    }

    @Override
    public Value<BigDecimalNumber> visit(BigDecimalNumber value) {
        return value;
    }

    @Override
    public Value<TimestampValue> visit(TimestampValue value) {
        return value;
    }

    private Optional<Value<BooleanValue>> handleSpecialUnaryCases(UnaryOperation operation) {

        if (operation.operator() == EXISTS) {

            if (operation.expression() instanceof SubTable subTable) {
                var table = resolver.resolve(subTable.commands(), externalRow).table();
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
                var table = resolver.resolve(subTable.commands(), externalRow).table();
                return Optional.of(processInTableOperation(operation.left().accept(this), table));
            }
        }

        return Optional.empty();
    }

    private BooleanValue processInTableOperation(Value<?> value, Table table) {
        var columnsCount = table.columns().size();

        if (columnsCount != 1) {
            throw new UnexpectedException("Expected exactly one column in table '%s'", table.name());
        }

        var columnData = table.dataStream()
                .map(row -> (Value<?>) row.get(0))
                .toList();

        var type = table.columns().get(0).type();

        return processInListOperation(value, new ExpressionList(columnData, type));
    }

    private BooleanValue processInListOperation(Value<?> value, ExpressionList list) {
        if (list.expressions().isEmpty()) {
            return new BooleanValue(false);
        }

        ModelUtils.theSameClasses(value.getClass(), list.expressions().get(0).getClass());

        var presents = list.expressions().stream()
                .anyMatch(ex -> ex.equals(value));

        return new BooleanValue(presents);
    }
}
