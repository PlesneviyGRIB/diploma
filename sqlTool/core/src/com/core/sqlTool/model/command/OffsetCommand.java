package com.core.sqlTool.model.command;

import com.core.sqlTool.exception.InvalidOffsetException;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.domain.Projection;

public record OffsetCommand(Integer offset) implements SimpleCommand {

    @Override
    public LazyTable run(LazyTable lazyTable, Projection projection) {

        if (offset < 0) {
            throw new InvalidOffsetException(offset);
        }

        return new LazyTable(lazyTable.name(), lazyTable.columns(), lazyTable.dataStream().skip(offset), lazyTable.externalRow());
    }

}
