package com.core.sqlTool.model.expression;

import com.core.sqlTool.model.command.domain.Command;

import java.util.List;
import java.util.Objects;

public record SubTable(List<Command> commands) implements Expression {

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubTable subTable = (SubTable) o;
        return Objects.equals(commands, subTable.commands);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(commands);
    }
}
