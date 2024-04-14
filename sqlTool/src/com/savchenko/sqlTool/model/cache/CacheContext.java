package com.savchenko.sqlTool.model.cache;

import com.savchenko.sqlTool.model.command.domain.Command;
import com.savchenko.sqlTool.model.resolver.CommandResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CacheContext {

    private final CacheStrategy strategy;

    private final Map<CacheKey, CommandResult> cache = new HashMap<>();

    public CacheContext(CacheStrategy strategy) {
        this.strategy = strategy;
    }

    public void cacheCommand(List<Command> commands, CommandResult commandResult) {
        if (strategy == CacheStrategy.PROPER) {
            cache.put(new CacheKey(commands), commandResult);
        }
    }

    public Optional<CommandResult> get(List<Command> commands) {
        return Optional.ofNullable(cache.get(new CacheKey(commands)));
    }

}
