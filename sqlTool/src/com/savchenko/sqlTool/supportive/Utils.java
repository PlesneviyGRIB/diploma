package com.savchenko.sqlTool.supportive;

import com.savchenko.sqlTool.model.Table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

public class Utils {
    public static Table renameTable(Table table, String tableName) {
        return new Table(tableName, table.columns(), table.data());
    }

    public static Comparable<?> readEntry(String object, Class<? extends Comparable<?>> targetClass) {
        try {
            if (Objects.isNull(object)) {
                return null;
            }
            if (targetClass.equals(String.class)) {
                return object;
            }
            if (targetClass.equals(Boolean.class)) {
                return Boolean.valueOf(object);
            }
            if (targetClass.equals(Integer.class)) {
                return Integer.valueOf(object);
            }
            if (targetClass.equals(Long.class)) {
                return Long.valueOf(object);
            }
            if (targetClass.equals(Double.class)) {
                return Double.valueOf(object);
            }
            if (targetClass.equals(BigDecimal.class)) {
                return BigDecimal.valueOf(Double.parseDouble(object));
            }
            if (targetClass.equals(Timestamp.class)) {
                try {
                    return Timestamp.valueOf(object);
                } catch (Exception e) {
                    return Timestamp.valueOf(object.split("\\+")[0]);
                }
            }
        } catch (Exception e) {
            System.out.printf("Unable to parse '%s' of type '%s'\n", object, targetClass.getTypeName());
        }
        return null;
    }
}
