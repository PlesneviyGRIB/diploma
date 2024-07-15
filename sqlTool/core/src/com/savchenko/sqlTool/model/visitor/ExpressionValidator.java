package com.savchenko.sqlTool.model.visitor;

import com.savchenko.sqlTool.exception.ComputedTypeException;
import com.savchenko.sqlTool.exception.IncorrectOperatorUsageException;
import com.savchenko.sqlTool.exception.UnsupportedTypeException;
import com.savchenko.sqlTool.model.domain.Column;
import com.savchenko.sqlTool.model.domain.ExternalHeaderRow;
import com.savchenko.sqlTool.model.expression.*;
import com.savchenko.sqlTool.model.operator.Operator;
import com.savchenko.sqlTool.utils.ModelUtils;
import org.apache.commons.collections4.ListUtils;

import java.util.List;

public class ExpressionValidator implements Expression.Visitor<Class<? extends Value<?>>> {

    private final List<Column> columns;

    public ExpressionValidator(List<Column> columns, ExternalHeaderRow externalRow) {
        ModelUtils.assertDifferentColumns(columns, externalRow.getColumns());
        this.columns = ListUtils.union(columns, externalRow.getColumns());
    }

    @Override
    public Class<? extends Value<?>> visit(ExpressionList list) {
        var argsClasses = list.expressions().stream()
                .map(expression -> expression.getClass())
                .toList();
        if (!argsClasses.isEmpty()) {
            var argType = (Class<? extends Value<?>>) list.expressions().get(0).getClass();
            assertSameClass(list, argType, list.type());
            assertSameClass(list, argsClasses.toArray(Class[]::new));
        }
        return list.type();
    }

    @Override
    public Class<? extends Value<?>> visit(SubTable table) {
        throw new UnsupportedTypeException("Can not process type of '%s' in such context", table.stringify());
    }

    @Override
    public Class<? extends Value<?>> visit(Column column) {
        return ModelUtils.resolveColumn(columns, column).type();
    }

    @Override
    public Class<? extends Value<?>> visit(UnaryOperation operation) {
        if (!operation.operator().isUnary()) {
            throw new IncorrectOperatorUsageException(operation.operator(), operation);
        }

        if (operation.operator() == Operator.EXISTS && operation.expression() instanceof SubTable) {
            return BooleanValue.class;
        }

        var type = operation.expression().accept(this);
        if (!ModelUtils.supportsOperator(type, operation.operator())) {
            throw new ComputedTypeException(operation);
        }
        return BooleanValue.class;
    }

    @Override
    public Class<? extends Value<?>> visit(BinaryOperation operation) {
        if (!operation.operator().isBinary()) {
            throw new IncorrectOperatorUsageException(operation.operator(), operation);
        }

        if (operation.operator() == Operator.IN && (operation.right() instanceof SubTable || operation.right() instanceof ExpressionList)) {
            if (operation.right() instanceof ExpressionList list) {
                var left = operation.left().accept(this);
                var right = operation.right().accept(this);
                assertSameClass(list, left, right);
            }
            return BooleanValue.class;
        }

        var left = operation.left().accept(this);
        var right = operation.right().accept(this);

        assertSameClass(operation, left, right);

        if (!ModelUtils.supportsOperator(left, operation.operator())) {
            throw new ComputedTypeException(operation);
        }

        return operation.operator().isLogic() ? BooleanValue.class : left;
    }

    @Override
    public Class<? extends Value<?>> visit(TernaryOperation operation) {
        if (!operation.operator().isTernary()) {
            throw new IncorrectOperatorUsageException(operation.operator(), operation);
        }
        var first = operation.first().accept(this);
        var second = operation.second().accept(this);
        var third = operation.third().accept(this);
        assertSameClass(operation, first, second, third);
        if (!ModelUtils.supportsOperator(first, operation.operator())) {
            throw new ComputedTypeException(operation);
        }
        return BooleanValue.class;
    }

    @Override
    public Class<? extends Value<?>> visit(NullValue value) {
        return NullValue.class;
    }

    @Override
    public Class<? extends Value<?>> visit(StringValue value) {
        return StringValue.class;
    }

    @Override
    public Class<? extends Value<?>> visit(BooleanValue value) {
        return BooleanValue.class;
    }

    @Override
    public Class<? extends Value<?>> visit(IntegerNumber operation) {
        return IntegerNumber.class;
    }

    @Override
    public Class<? extends Value<?>> visit(LongNumber value) {
        return LongNumber.class;
    }

    @Override
    public Class<? extends Value<?>> visit(FloatNumber value) {
        return FloatNumber.class;
    }

    @Override
    public Class<? extends Value<?>> visit(DoubleNumber value) {
        return DoubleNumber.class;
    }

    @Override
    public Class<? extends Value<?>> visit(BigDecimalNumber value) {
        return BigDecimalNumber.class;
    }

    @Override
    public Class<? extends Value<?>> visit(TimestampValue value) {
        return TimestampValue.class;
    }

    private void assertSameClass(Expression expression, Class<? extends Value<?>>... classes) {
        if (!ModelUtils.theSameClasses(classes)) {
            throw new ComputedTypeException(expression);
        }
    }
}
