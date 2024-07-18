package com.client.sqlTool.command;

import com.client.sqlTool.domain.Column;
import lombok.Getter;

@Getter
public class Order implements Command {

    private final Column column;

    private final boolean asc;

    private Order(Column column, boolean asc) {
        this.column = column;
        this.asc = asc;
    }

    public Order asc() {
        return new Order(this.column, true);
    }

    public Order desc() {
        return new Order(this.column, false);
    }

    public static Order of(Column column) {
        return new Order(column, true);
    }

}
