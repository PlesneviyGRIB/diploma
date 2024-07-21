package com.core.sqlTool.model.cache;

import com.core.sqlTool.model.resolver.CommandResult;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class CacheContext {

    private final CacheStrategy strategy;

    private final Map<CacheKey, CommandResult> cache = new HashMap<>();

    public void put(CacheKey cacheKey, CommandResult commandResult) {
        if (strategy == CacheStrategy.PROPER) {
            cache.put(cacheKey, commandResult);
            return;
        }
        if (strategy == CacheStrategy.PHONY) {
            var phonyCommandResult = new CommandResult(null, commandResult.calculatorEntry());
            cache.put(cacheKey, phonyCommandResult);
        }
    }

    public Optional<CommandResult> get(CacheKey cacheKey) {
        return Optional.ofNullable(cache.get(cacheKey));
    }

}