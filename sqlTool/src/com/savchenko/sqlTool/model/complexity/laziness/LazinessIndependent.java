package com.savchenko.sqlTool.model.complexity.laziness;

import com.savchenko.sqlTool.model.command.domain.Command;

public interface LazinessIndependent extends Command {

    default  <T> T accept(LazyVisitor<T> visitor) {
        return visitor.visit(this);
    }

}