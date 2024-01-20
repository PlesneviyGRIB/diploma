package com.savchenko.sqlTool.model.command.supportive.predicate;

import com.savchenko.sqlTool.model.Column;

import java.util.List;

public interface Predicate {
    boolean test(List<Column> columns, List<Comparable<?>> row);
}
