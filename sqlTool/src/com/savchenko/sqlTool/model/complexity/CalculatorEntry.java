package com.savchenko.sqlTool.model.complexity;

import static java.lang.String.format;

public interface CalculatorEntry extends TotalCalculated {

    String stringify(String prefix);

    default String toRow(String prefix, String template, Object... values) {
        return format(format("%s", prefix) + template, values);
    }

}
