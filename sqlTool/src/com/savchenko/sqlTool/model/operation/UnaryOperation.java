package com.savchenko.sqlTool.model.operation;

import com.savchenko.sqlTool.model.operator.LogicOperator;
import com.savchenko.sqlTool.model.structure.Column;
import com.savchenko.sqlTool.query.ColumnRef;

public record UnaryOperation(LogicOperator operator, Column column) implements Expression {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
