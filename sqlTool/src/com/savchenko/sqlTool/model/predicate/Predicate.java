package com.savchenko.sqlTool.model.predicate;

import com.savchenko.sqlTool.model.expression.Value;
import com.savchenko.sqlTool.model.structure.Column;

import java.util.List;

public interface Predicate {
    Predicate TRUE = (columns, row) -> true;
    Predicate FALSE = (columns, row) -> false;
    boolean test(List<Column> columns, List<Value<?>> row);
}
