package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.domain.Column;

import java.util.Objects;

public record Order(Column column, boolean reverse) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return reverse == order.reverse && Objects.equals(column, order.column);
    }

    @Override
    public int hashCode() {
        return Objects.hash(column, reverse);
    }
}
