package com.core.sqlTool.utils;

import com.client.sqlTool.expression.Operator;
import com.core.sqlTool.exception.ColumnNotFoundException;
import com.core.sqlTool.exception.UnexpectedException;
import com.core.sqlTool.exception.UnsupportedTypeException;
import com.core.sqlTool.exception.ValidationException;
import com.core.sqlTool.model.domain.Column;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.domain.Row;
import com.core.sqlTool.model.expression.*;
import com.core.sqlTool.model.visitor.ExpressionValidator;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.String.format;

public class ModelUtils {

    public static LazyTable renameTable(LazyTable lazyTable, String tableName) {

        Function<Column, String> columnIdentifier = column -> {
            var tokens = Arrays.asList(column.toString().split("\\."));
            return String.join(".", tokens.subList(1, tokens.size()));
        };

        var identityMap = lazyTable.columns().stream()
                .collect(Collectors.toMap(columnIdentifier, c -> 1, Integer::sum));

        var targetColumns = lazyTable.columns().stream()
                .map(column -> {
                    var identifier = columnIdentifier.apply(column);
                    if (identityMap.get(identifier) != 1) {
                        identifier = format("%s.%s", column.getTableName(), identifier);
                    }
                    return new Column(tableName, identifier, column.getColumnType());
                }).toList();

        return new LazyTable(tableName, targetColumns, lazyTable.dataStream(), lazyTable.externalRow());
    }

    public static Row emptyRow(LazyTable lazyTable) {
        var size = lazyTable.columns().size();
        var data = new ArrayList<Value<?>>(size);
        IntStream.range(0, size).forEach(index -> data.add(index, new NullValue()));
        return new Row(data);
    }

    public static Row typeSafeEmptyRow(LazyTable lazyTable) {
        var columns = lazyTable.columns();
        var size = columns.size();
        var data = new ArrayList<Value<?>>(size);
        IntStream.range(0, size).forEach(index -> data.add(index, getDefaultValueByType(columns.get(index).getColumnType())));
        return new Row(data);
    }

    public static List<Value<?>> toSingleTypeValues(List<Value<?>> list) {
        if (CollectionUtils.isEmpty(list)) {
            return List.of();
        }
        try {
            var wipedTypeList = (Object) list;
            return (List<Value<?>>) wipedTypeList;
        } catch (RuntimeException e) {
            var f = 1;
            throw new RuntimeException(e);
        }
    }

    public static Value<?> readEntry(String object, Class<? extends Value<?>> targetClass) {
        try {
            if (Objects.isNull(object)) {
                return new NullValue();
            }
            if (targetClass.equals(StringValue.class)) {
                return new StringValue(object);
            }
            if (targetClass.equals(BooleanValue.class)) {
                return new BooleanValue(Boolean.parseBoolean(object));
            }
            if (targetClass.equals(NumberValue.class)) {
                return new NumberValue((int) Long.parseLong(object));
            }
            if (targetClass.equals(FloatNumberValue.class)) {
                return new FloatNumberValue(Float.parseFloat(object));
            }
            if (targetClass.equals(TimestampValue.class)) {
                try {
                    return new TimestampValue(Timestamp.valueOf(object));
                } catch (Exception e) {
                    return new TimestampValue(Timestamp.valueOf(object.split("\\+")[0]));
                }
            }
        } catch (Exception e) {
            System.err.printf("Unable to parse '%s' of columnType '%s'\n", object, targetClass.getTypeName());
        }
        return null;
    }

    public static Class<? extends Value<?>> getWrapper(Class<?> clazz) {
        if (clazz.equals(String.class) || clazz.equals(Object.class)) {
            return StringValue.class;
        } else if (clazz.equals(Boolean.class)) {
            return BooleanValue.class;
        } else if (clazz.equals(Integer.class)) {
            return NumberValue.class;
        } else if (clazz.equals(Long.class)) {
            return NumberValue.class;
        } else if (clazz.equals(BigDecimal.class)) {
            return FloatNumberValue.class;
        } else if (clazz.equals(Float.class)) {
            return FloatNumberValue.class;
        } else if (clazz.equals(Double.class)) {
            return FloatNumberValue.class;
        } else if (clazz.equals(Timestamp.class)) {
            return TimestampValue.class;
        }

        throw new UnsupportedTypeException("Unable to process value of columnType '%s'", clazz.getSimpleName());
    }

    public static int resolveColumnIndex(List<Column> columns, Column column) {
        var index = columns.indexOf(column);
        if (index == -1) {
            throw new ColumnNotFoundException(column, columns);
        }
        return index;
    }

    public static Column resolveColumn(List<Column> columns, Column column) {
        var index = resolveColumnIndex(columns, column);
        return columns.get(index);
    }

    public static Column getColumnFromExpression(Expression expression, LazyTable lazyTable, Integer index, ExpressionValidator expressionValidator) {
        if (expression instanceof Column column) {
            return ModelUtils.resolveColumn(lazyTable.columns(), column);
        }

        var columnName = "column_%s".formatted(index);
        var columnType = expression.accept(expressionValidator);

        return new Column(lazyTable.name(), columnName, columnType);
    }

    public static int compareValues(Value<?> value1, Value<?> value2, Class<? extends Value> targetType) {
        if (value1 instanceof NullValue && !(value2 instanceof NullValue)) {
            return 1;
        }
        if (!(value1 instanceof NullValue) && value2 instanceof NullValue) {
            return -1;
        }
        if (value1 instanceof NullValue nv1 && value2 instanceof NullValue nv2) {
            return nv1.compareTo(nv2);
        }
        return targetType.cast(value1).compareTo(targetType.cast(value2));
    }

    public static boolean supportsOperator(Class<? extends Value<?>> clazz, Operator operator) {
        Function<List<Operator>, Boolean> check = list ->
                CollectionUtils.union(List.of(Operator.EXISTS, Operator.IN, Operator.IS_NULL, Operator.EQ, Operator.NOT_EQ, Operator.GREATER_OR_EQ, Operator.LESS_OR_EQ, Operator.GREATER, Operator.LESS), list)
                        .stream().anyMatch(o -> o.equals(operator));

        if (clazz.equals(NullValue.class)) {
            return check.apply(List.of(Operator.AND, Operator.BETWEEN, Operator.OR));
        }
        if (clazz.equals(StringValue.class)) {
            return check.apply(List.of(Operator.BETWEEN, Operator.PLUS, Operator.LIKE));
        }
        if (clazz.equals(BooleanValue.class)) {
            return List.of(Operator.AND, Operator.OR, Operator.EXISTS, Operator.IN, Operator.IS_NULL, Operator.EQ, Operator.NOT_EQ, Operator.NOT).contains(operator);
        }
        if (clazz.equals(NumberValue.class)) {
            return check.apply(List.of(Operator.BETWEEN, Operator.PLUS, Operator.MINUS, Operator.MULTIPLY, Operator.DIVISION, Operator.MOD));
        }
        if (clazz.equals(FloatNumberValue.class)) {
            return check.apply(List.of(Operator.BETWEEN, Operator.PLUS, Operator.MINUS, Operator.MULTIPLY, Operator.DIVISION));
        }
        if (clazz.equals(TimestampValue.class)) {
            return check.apply(List.of(Operator.BETWEEN, Operator.PLUS, Operator.MINUS));
        }
        throw new ValidationException("Unexpected columnType '%s'", clazz.getTypeName());
    }

    public static boolean theSameClasses(Class... classes) {
        var type = (Class) Arrays.stream(classes).toArray()[0];
        return Arrays.stream(classes).allMatch(c -> c.equals(type));
    }

    public static void assertDifferentColumns(List<Column> columns1, List<Column> columns2) {
        columns1.stream()
                .filter(columns2::contains)
                .findAny()
                .ifPresent(c -> {
                    throw new UnexpectedException(
                            "Ambiguity found: Column columnName for sub tableName and parent tableName is the same '%s'. Try to rename sub or parent tableName",
                            c.stringify());
                });
    }

    public static String sqlPatternToJavaPattern(String pattern) {
        return pattern
                .replace(".", "\\.")
                .replace("?", ".")
                .replace("%", ".*");
    }

    private static Value<?> getDefaultValueByType(Class<? extends Value<?>> valueClass) {
        if (valueClass.equals(StringValue.class)) {
            return new StringValue("");
        } else if (valueClass.equals(BooleanValue.class)) {
            return new BooleanValue(false);
        } else if (valueClass.equals(NumberValue.class)) {
            return new NumberValue(0);
        } else if (valueClass.equals(FloatNumberValue.class)) {
            return new FloatNumberValue(0F);
        } else if (valueClass.equals(TimestampValue.class)) {
            return new TimestampValue(new Timestamp(0));
        }
        throw new UnsupportedTypeException("Unable to process value of columnType '%s'", valueClass.getSimpleName());
    }

}
