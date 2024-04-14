package com.savchenko.sqlTool.model.resolver;

import com.savchenko.sqlTool.model.command.domain.Command;

import java.util.List;
import java.util.Optional;

public class CacheContext {

    public void cacheCommand(List<Command> previous, Command command) {

    }

    public Optional<?> get(List<Command> previous, Command command) {
        return Optional.empty();
    }

}
