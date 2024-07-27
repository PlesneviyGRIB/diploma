package com.core.sqlTool.model.domain;

import com.core.sqlTool.model.expression.Expression;
import com.core.sqlTool.model.expression.Value;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(exclude = {"columnType"})
public final class Column implements Expression {

    private final String tableName;

    private final String columnName;

    private final Class<? extends Value<?>> columnType;

    @Override
    public String toString() {
        if (Objects.isNull(tableName)) {
            return columnName;
        }
        return "%s.%s".formatted(tableName, columnName);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }


}
