package com.savchenko.sqlTool.utils;

import com.savchenko.sqlTool.exception.ColumnNotFoundException;
import com.savchenko.sqlTool.exception.UnsupportedTypeException;
import com.savchenko.sqlTool.model.expression.*;
import com.savchenko.sqlTool.model.structure.Column;
import com.savchenko.sqlTool.model.structure.Table;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class ModelUtils {
    public static Table renameTable(Table table, String tableName) {
        return new Table(tableName, table.columns(), table.data());
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
            return DoubleNumber.class;
        }else if (clazz.equals(Double.class)) {
            return DoubleNumber.class;
        } else if (clazz.equals(BigDecimal.class)) {
            return BigDecimalNumber.class;
        } else if (clazz.equals(Timestamp.class)) {
            return TimestampValue.class;
        }
        throw new UnsupportedTypeException();
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
        // TODO
        if(value1 instanceof NullValue nullValue) {
            return nullValue.compareTo(value2);
        }
        if(value2 instanceof NullValue nullValue) {
            return nullValue.compareTo(value1);
        }
        return targetType.cast(value1).compareTo(targetType.cast(value2));
    }

}
