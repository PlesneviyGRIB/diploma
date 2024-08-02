package com.core.sqlTool.utils;

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

}
