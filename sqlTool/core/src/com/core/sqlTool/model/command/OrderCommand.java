package com.core.sqlTool.model.command;

import com.core.sqlTool.model.domain.Column;

import java.util.Objects;

public record OrderCommand(Column column, boolean reverse) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderCommand order = (OrderCommand) o;
        return reverse == order.reverse && Objects.equals(column, order.column);
    }

    @Override
    public int hashCode() {
        return Objects.hash(column, reverse);
    }
}
