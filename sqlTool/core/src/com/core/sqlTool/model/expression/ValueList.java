package com.core.sqlTool.model.expression;

import java.util.List;

public record ValueList(List<? extends Value<?>> expressions, Class<? extends Value<?>> type) implements Expression {

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}
