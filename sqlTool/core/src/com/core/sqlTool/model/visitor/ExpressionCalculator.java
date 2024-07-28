package com.core.sqlTool.model.visitor;

import com.client.sqlTool.expression.Operator;
import com.core.sqlTool.exception.UnexpectedException;
import com.core.sqlTool.exception.UnexpectedExpressionException;
import com.core.sqlTool.model.domain.Column;
import com.core.sqlTool.model.domain.ExternalHeaderRow;
import com.core.sqlTool.model.domain.HeaderRow;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.expression.*;
import com.core.sqlTool.model.resolver.Resolver;
import com.core.sqlTool.utils.ModelUtils;

import java.util.Objects;
import java.util.Optional;

public class ExpressionCalculator implements Expression.Visitor<Value<?>> {

    private final Resolver resolver;

    private final ExternalHeaderRow mergedExternalHeaderRow;

    public ExpressionCalculator(Resolver resolver, HeaderRow headerRow, ExternalHeaderRow externalRow) {
        this.resolver = resolver;
        this.mergedExternalHeaderRow = externalRow.merge(new ExternalHeaderRow(headerRow.columns(), headerRow.row()));
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
        if (op == Operator.EXISTS) {
            return new BooleanValue(!(value instanceof NullValue));
        }
        if (op == Operator.IS_NULL) {
            return new BooleanValue(value instanceof NullValue);
        }
        if (op == Operator.NOT) {
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
    public Value<NumberValue> visit(NumberValue value) {
        return value;
    }

    @Override
    public Value<FloatNumberValue> visit(FloatNumberValue value) {
        return value;
    }

    @Override
    public Value<TimestampValue> visit(TimestampValue value) {
        return value;
    }

    @Override
    public Value<?> visit(NamedExpression value) {
        return value.expression().accept(this);
    }

    private Optional<Value<BooleanValue>> handleSpecialUnaryCases(UnaryOperation operation) {

        if (operation.operator() == Operator.EXISTS) {

            if (operation.expression() instanceof SubTable subTable) {
                var table = resolver.resolve(subTable.commands(), mergedExternalHeaderRow).lazyTable();
                return Optional.of(new BooleanValue(table.dataStream().findAny().isPresent()));
            }

        }

        return Optional.empty();
    }

    private Optional<Value<?>> handleSpecialBinaryCases(BinaryOperation operation) {

        if (operation.operator() == Operator.IN) {

            if (operation.right() instanceof ExpressionList list) {
                return Optional.of(processInListOperation(operation.left().accept(this), list));
            }

            if (operation.right() instanceof SubTable subTable) {
                var table = resolver.resolve(subTable.commands(), mergedExternalHeaderRow).lazyTable();
                return Optional.of(processInTableOperation(operation.left().accept(this), table));
            }
        }

        return Optional.empty();
    }

    private BooleanValue processInTableOperation(Expression expression, LazyTable lazyTable) {
        var columnsCount = lazyTable.columns().size();

        if (columnsCount != 1) {
            throw new UnexpectedException("Expected exactly one columnName in tableName '%s'", lazyTable.name());
        }

        var columnData = lazyTable.dataStream()
                .map(row -> row.values().get(0))
                .toList();

        return processInListOperation(expression, new ExpressionList(columnData));
    }

    private BooleanValue processInListOperation(Expression expression, ExpressionList list) {
        if (list.expressions().isEmpty()) {
            return new BooleanValue(false);
        }

        var value = expression.accept(this);
        var presents = list.expressions().stream()
                .map(e -> e.accept(this))
                .anyMatch(val -> val.equals(value));

        return new BooleanValue(presents);
    }

}
