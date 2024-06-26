package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.command.domain.SimpleCalculedCommand;
import com.savchenko.sqlTool.model.complexity.CalculatorEntry;
import com.savchenko.sqlTool.model.domain.LazyTable;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.index.Index;
import com.savchenko.sqlTool.utils.ModelUtils;

import java.util.Objects;

public class ConstructIndex implements SimpleCalculedCommand {

    private final Index index;

    public ConstructIndex(Index index) {
        this.index = index;
    }

    @Override
    public LazyTable run(LazyTable lazyTable, Projection projection, CalculatorEntry calculatorEntry) {

        index.getColumns().forEach(column -> ModelUtils.resolveColumn(lazyTable.columns(), column));

        return lazyTable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstructIndex that = (ConstructIndex) o;
        return Objects.equals(index, that.index);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(index);
    }
}
