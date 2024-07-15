package com.core.sqlTool.model.command;

import com.core.sqlTool.model.command.domain.SimpleCalculedCommand;
import com.core.sqlTool.model.complexity.CalculatorEntry;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.domain.Projection;
import com.core.sqlTool.model.index.Index;
import com.core.sqlTool.utils.ModelUtils;

import java.util.Objects;

public class ConstructIndexCommand implements SimpleCalculedCommand {

    private final Index index;

    public ConstructIndexCommand(Index index) {
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
        ConstructIndexCommand that = (ConstructIndexCommand) o;
        return Objects.equals(index, that.index);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(index);
    }
}
