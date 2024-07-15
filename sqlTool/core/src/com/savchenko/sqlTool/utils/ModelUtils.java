package com.savchenko.sqlTool.utils;

import com.savchenko.sqlTool.exception.ColumnNotFoundException;
import com.savchenko.sqlTool.exception.UnexpectedException;
import com.savchenko.sqlTool.exception.UnsupportedTypeException;
import com.savchenko.sqlTool.exception.ValidationException;
import com.savchenko.sqlTool.model.domain.Column;
import com.savchenko.sqlTool.model.domain.LazyTable;
import com.savchenko.sqlTool.model.domain.Row;
import com.savchenko.sqlTool.model.expression.*;
import com.savchenko.sqlTool.model.operator.Operator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.savchenko.sqlTool.model.operator.Operator.*;
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
                        identifier = format("%s.%s", column.table(), identifier);
                    }
                    return new Column(identifier, tableName, column.type());
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
        IntStream.range(0, size).forEach(index -> data.add(index, getDefaultValueByType(columns.get(index).type())));
        return new Row(data);
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
            if (targetClass.equals(IntegerNumber.class)) {
                return new IntegerNumber(Integer.parseInt(object));
            }
            if (targetClass.equals(LongNumber.class)) {
                return new LongNumber(Long.parseLong(object));
            }
            if (targetClass.equals(FloatNumber.class)) {
                return new FloatNumber(Float.parseFloat(object));
            }
            if (targetClass.equals(DoubleNumber.class)) {
                return new DoubleNumber(Double.parseDouble(object));
            }
            if (targetClass.equals(BigDecimalNumber.class)) {
                return new BigDecimalNumber(BigDecimal.valueOf(Double.parseDouble(object)));
            }
            if (targetClass.equals(TimestampValue.class)) {
                try {
                    return new TimestampValue(Timestamp.valueOf(object));
                } catch (Exception e) {
                    return new TimestampValue(Timestamp.valueOf(object.split("\\+")[0]));
                }
            }
        } catch (Exception e) {
            System.err.printf("Unable to parse '%s' of type '%s'\n", object, targetClass.getTypeName());
        }
        return null;
    }

    public static Class<? extends Value<?>> getWrapper(Class<?> clazz) {
        if (clazz.equals(String.class)) {
            return StringValue.class;
        } else if (clazz.equals(Boolean.class)) {
            return BooleanValue.class;
        } else if (clazz.equals(Integer.class)) {
            return IntegerNumber.class;
        } else if (clazz.equals(Long.class)) {
            return LongNumber.class;
        } else if (clazz.equals(Float.class)) {
            return FloatNumber.class;
        } else if (clazz.equals(Double.class)) {
            return DoubleNumber.class;
        } else if (clazz.equals(BigDecimal.class)) {
            return BigDecimalNumber.class;
        } else if (clazz.equals(Timestamp.class)) {
            return TimestampValue.class;
        }
        throw new UnsupportedTypeException("Unable to process value of type '%s'", clazz.getSimpleName());
    }

    public static int resolveColumnIndex(List<Column> columns, Column column) {
        return IntStream.range(0, columns.size()).filter(i -> {
            var c = columns.get(i);
            return c.name().equals(column.name()) && c.table().equals(column.table());
        }).findFirst().orElseThrow(() -> new ColumnNotFoundException(column, columns));
    }

    public static Column resolveColumn(List<Column> columns, Column column) {
        var index = resolveColumnIndex(columns, column);
        return columns.get(index);
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
                CollectionUtils.union(List.of(EXISTS, IN, IS_NULL, EQ, NOT_EQ, GREATER_OR_EQ, LESS_OR_EQ, GREATER, LESS), list)
                        .stream().anyMatch(o -> o.equals(operator));

        if (clazz.equals(NullValue.class)) {
            return check.apply(List.of(AND, BETWEEN, OR));
        }
        if (clazz.equals(StringValue.class)) {
            return check.apply(List.of(BETWEEN, PLUS, LIKE));
        }
        if (clazz.equals(BooleanValue.class)) {
            return List.of(AND, OR, EXISTS, IN, IS_NULL, EQ, NOT_EQ, NOT).contains(operator);
        }
        if (clazz.equals(IntegerNumber.class)) {
            return check.apply(List.of(BETWEEN, PLUS, MINUS, MULTIPLY, DIVISION, MOD));
        }
        if (clazz.equals(LongNumber.class)) {
            return check.apply(List.of(BETWEEN, PLUS, MINUS, MULTIPLY, DIVISION));
        }
        if (clazz.equals(FloatNumber.class)) {
            return check.apply(List.of(BETWEEN, PLUS, MINUS, MULTIPLY, DIVISION));
        }
        if (clazz.equals(DoubleNumber.class)) {
            return check.apply(List.of(BETWEEN, PLUS, MINUS, MULTIPLY, DIVISION));
        }
        if (clazz.equals(BigDecimalNumber.class)) {
            return check.apply(List.of(BETWEEN, PLUS, MINUS, MULTIPLY, DIVISION));
        }
        if (clazz.equals(TimestampValue.class)) {
            return check.apply(List.of(BETWEEN, PLUS, MINUS));
        }
        throw new ValidationException("Unexpected type '%s'", clazz.getTypeName());
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
                            "Ambiguity found: Column name for sub table and parent table is the same '%s'. Try to rename sub or parent table",
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
        } else if (valueClass.equals(IntegerNumber.class)) {
            return new IntegerNumber(0);
        } else if (valueClass.equals(LongNumber.class)) {
            return new LongNumber(0L);
        } else if (valueClass.equals(FloatNumber.class)) {
            return new FloatNumber(0F);
        } else if (valueClass.equals(DoubleNumber.class)) {
            return new DoubleNumber(0D);
        } else if (valueClass.equals(BigDecimalNumber.class)) {
            return new BigDecimalNumber(new BigDecimal(0));
        } else if (valueClass.equals(TimestampValue.class)) {
            return new TimestampValue(new Timestamp(0));
        }
        throw new UnsupportedTypeException("Unable to process value of type '%s'", valueClass.getSimpleName());
    }

}
