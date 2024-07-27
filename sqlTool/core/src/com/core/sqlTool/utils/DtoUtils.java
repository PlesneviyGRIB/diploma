package com.core.sqlTool.utils;

import com.core.sqlTool.exception.InvalidColumnNameException;
import org.apache.commons.lang3.tuple.Pair;

public class DtoUtils {

    public static Pair<String, String> parseTableAndColumnName(String columnFullName) {

        var tokens = columnFullName.split("\\.");

        if (tokens.length == 1) {
            return Pair.of(null, tokens[0]);
        }

        if (tokens.length == 2) {
            return Pair.of(tokens[0], tokens[1]);
        }

        throw new InvalidColumnNameException(columnFullName);
    }

}
