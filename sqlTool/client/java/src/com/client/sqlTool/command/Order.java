package com.client.sqlTool.command;

import com.client.sqlTool.domain.Column;
import com.client.sqlTool.expression.Expression;
import lombok.Getter;

@Getter
public class Order implements Command {

    private final Expression expression;

    private final boolean asc;

    private Order(Expression expression, boolean asc) {
        this.expression = expression;
        this.asc = asc;
    }

    public Order asc() {
        return new Order(this.expression, true);
    }

    public Order desc() {
        return new Order(this.expression, false);
    }

    public static Order of(Column column) {
        return new Order(column, true);
    }

}
