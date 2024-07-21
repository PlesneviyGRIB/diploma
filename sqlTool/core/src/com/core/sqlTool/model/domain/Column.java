package com.core.sqlTool.model.domain;

import com.core.sqlTool.model.expression.Expression;
import com.core.sqlTool.model.expression.Value;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(exclude = {"columnType"})
public class Column implements Expression {

    private final String tableName;

    private final String columnName;

    private final Class<? extends Value<?>> columnType;

    @Override
    public String toString() {
        return tableName + "." + columnName;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }


}
