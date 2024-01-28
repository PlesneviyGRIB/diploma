package com.savchenko.sqlTool.query;

import com.savchenko.sqlTool.model.command.Command;
import com.savchenko.sqlTool.model.command.From;
import com.savchenko.sqlTool.model.command.Select;
import com.savchenko.sqlTool.repository.Projection;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class CommandsValidator {
    private final List<Command> commands;

    public CommandsValidator(List<Command> commands) {
        this.commands = commands;
    }

    public CommandsValidator validate(Projection projection) {
        Function<Predicate<Command>, List<Command>> getCommands = predicate -> commands.stream().filter(predicate).toList();

        var selectCommands = getCommands.apply(column -> column instanceof Select);
        if(selectCommands.size() != 1) {
            throw new RuntimeException("Only one SELECT command allowed!");
        }
        if(!(commands.get(0) instanceof Select)) {
            throw new RuntimeException("Expected SELECT command at first position!");
        }
        var fromCommands = getCommands.apply(column -> column instanceof From);
        if(fromCommands.isEmpty()) {
            throw new RuntimeException("At least one FROM command expected!");
        }
        if(!(commands.get(1) instanceof From)) {
            throw new RuntimeException("Expected FROM command at second position!");
        }
        commands.forEach(c -> c.validate(projection));
        return this;
    };

    public List<Command> getNormalized() {
        return commands.stream().sorted((c1, c2) -> {
            if(c1 instanceof Select) return 1;
            if(c2 instanceof Select) return -1;
            return 0;
        }).toList();
    };
}
