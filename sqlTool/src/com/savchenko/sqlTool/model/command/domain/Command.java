package com.savchenko.sqlTool.model.command.domain;

import com.savchenko.sqlTool.model.complexity.laziness.ClauseReducer;
import com.savchenko.sqlTool.model.complexity.laziness.LazinessIndependent;
import com.savchenko.sqlTool.model.complexity.laziness.Lazy;
import com.savchenko.sqlTool.model.complexity.laziness.LazyConcealer;

public interface Command {

    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(SimpleCommand command);

        T visit(SimpleCalculedCommand command);

        T visit(ComplexCalculedCommand command);
    }

    interface LazyVisitor<T> {
        T visit(Lazy command);

        T visit(LazyConcealer command);

        T visit(LazinessIndependent command);

        T visit(ClauseReducer command);
    }

}
