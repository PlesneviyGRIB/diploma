package com.core.sqlTool.utils;

import com.core.sqlTool.exception.UnexpectedException;
import com.core.sqlTool.model.expression.*;

import static java.lang.String.format;

public class PrinterUtils {

    public static String green(Object object) {
        return format("\u001B[32m%s\u001B[0m", object);
    }

    public static String red(Object object) {
        return format("\u001B[31m%s\u001B[0m", object);
    }

    public static String blue(Object object) {
        return format("\u001B[34m%s\u001B[0m", object);
    }

    public static String valueToString(Class<? extends Value<?>> valueClass) {
        if (valueClass.equals(BooleanValue.class)) {
            return "boolean";
        }
        if (valueClass.equals(FloatNumberValue.class)) {
            return "float";
        }
        if (valueClass.equals(NullValue.class)) {
            return "null";
        }
        if (valueClass.equals(NumberValue.class)) {
            return "number";
        }
        if (valueClass.equals(StringValue.class)) {
            return "string";
        }
        if (valueClass.equals(TimestampValue.class)) {
            return "timestamp";
        }
        throw new UnexpectedException();
    }

    public static String fixedWidth(String string, Integer width) {
        var format = "%" + width + "." + width + "s";
        return format.formatted(string);
    }

}
