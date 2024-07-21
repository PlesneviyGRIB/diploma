package com.core.sqlTool.model.cache;

import com.core.sqlTool.model.command.Command;
import com.core.sqlTool.model.domain.ExternalHeaderRow;

import java.util.List;
import java.util.Objects;

public record CacheKey(List<Command> commands, ExternalHeaderRow externalRow) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheKey cacheKey = (CacheKey) o;
        return Objects.equals(commands, cacheKey.commands) && Objects.equals(externalRow, cacheKey.externalRow);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commands, externalRow);
    }
}
