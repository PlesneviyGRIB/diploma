package com.core.sqlTool.model.visitor;

import com.client.sqlTool.expression.Operator;
import com.core.sqlTool.exception.ComputedTypeException;
import com.core.sqlTool.exception.IncorrectOperatorUsageException;
import com.core.sqlTool.exception.UnexpectedExpressionException;
import com.core.sqlTool.model.domain.Column;
import com.core.sqlTool.model.domain.ExternalHeaderRow;
import com.core.sqlTool.model.expression.*;
import com.core.sqlTool.utils.ModelUtils;
import com.core.sqlTool.utils.OperatorUtils;
import com.core.sqlTool.utils.ValidationUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.ListUtils;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;

public class ExpressionResultTypeResolver implements Expression.Visitor<Class<? extends Value<?>>> {

    private final List<Column> columns;

    public ExpressionResultTypeResolver(List<Column> columns, ExternalHeaderRow externalRow) {
        ValidationUtils.assertDifferentColumns(columns, externalRow.columns());
        this.columns = ListUtils.union(columns, externalRow.columns());
    }

    @Override
    public Class<? extends Value<?>> visit(ExpressionList expressionList) {
        var type = ((ParameterizedType) expressionList.expressions().getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        var rawType = TypeToken.get(type).getRawType();
        return (Class<? extends Value<?>>) rawType;
    }

    @Override
    public Class<? extends Value<?>> visit(SubTable table) {
        throw new UnexpectedExpressionException(table);
    }

    @Override
    public Class<? extends Value<?>> visit(Column column) {
        return ModelUtils.resolveColumn(columns, column).getColumnType();
    }

    @Override
    public Class<? extends Value<?>> visit(UnaryOperation operation) {
        if (!OperatorUtils.isUnary(operation.operator())) {
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
        if (!OperatorUtils.isBinary(operation.operator())) {
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

        return OperatorUtils.isLogic(operation.operator()) ? BooleanValue.class : left;
    }

    @Override
    public Class<? extends Value<?>> visit(TernaryOperation operation) {
        if (!OperatorUtils.isTernary(operation.operator())) {
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
    public Class<? extends Value<?>> visit(NumberValue operation) {
        return NumberValue.class;
    }

    @Override
    public Class<? extends Value<?>> visit(FloatNumberValue value) {
        return FloatNumberValue.class;
    }

    @Override
    public Class<? extends Value<?>> visit(TimestampValue value) {
        return TimestampValue.class;
    }

    @Override
    public Class<? extends Value<?>> visit(NamedExpression value) {
        return value.expression().accept(this);
    }

    @SafeVarargs
    private void assertSameClass(Expression expression, Class<? extends Value<?>>... classes) {

        var type = (Class<?>) Arrays.stream(classes).toArray()[0];
        var classesAreSame = Arrays.stream(classes).allMatch(c -> c.equals(type));

        if (!classesAreSame) {
            throw new ComputedTypeException(expression);
        }
    }

}
