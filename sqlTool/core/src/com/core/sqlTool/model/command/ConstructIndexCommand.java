package com.core.sqlTool.model.command;

import com.core.sqlTool.model.complexity.CalculatorEntry;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.domain.Projection;
import com.core.sqlTool.model.index.Index;

public record ConstructIndexCommand(Index index) implements CalculatedCommand {

    @Override
    public LazyTable run(LazyTable lazyTable, Projection projection, CalculatorEntry calculatorEntry) {

        throw new UnsupportedOperationException("Not implemented yet");

//        //index.getColumns().forEach(column -> ModelUtils.resolveColumn(lazyTable.columns(), column));
//
//        return lazyTable;
    }

}
