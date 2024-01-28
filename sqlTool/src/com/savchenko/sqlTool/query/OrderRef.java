package com.savchenko.sqlTool.query;

public class OrderRef {
    public final ColumnRef columnRef;
    public final boolean reverse;

    public OrderRef(ColumnRef columnRef) {
        this.columnRef = columnRef;
        this.reverse = false;
    }

    private OrderRef(ColumnRef columnRef, boolean reverse) {
        this.columnRef = columnRef;
        this.reverse = reverse;
    }

    public OrderRef asc() {
        return new OrderRef(this.columnRef, false);
    }

    public OrderRef desc() {
        return new OrderRef(this.columnRef, true);
    }
}
