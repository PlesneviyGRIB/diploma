package com.core.sqlTool.model.complexity;

import java.util.List;

public record Calculator(List<CalculatorEntry> entries) {

    public Integer getComplexity() {
        return 0;
    }

}
