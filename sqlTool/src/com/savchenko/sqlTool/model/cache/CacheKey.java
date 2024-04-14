package com.savchenko.sqlTool.model.cache;

import com.savchenko.sqlTool.model.command.domain.Command;
import com.savchenko.sqlTool.model.domain.ExternalRow;

import java.util.List;
import java.util.Objects;

public record CacheKey(List<Command> commands, ExternalRow externalRow) {

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
