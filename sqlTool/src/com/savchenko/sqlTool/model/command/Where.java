package com.savchenko.sqlTool.model.command;

import com.savchenko.sqlTool.model.Table;
import com.savchenko.sqlTool.model.command.supportive.predicate.Predicate;
import com.savchenko.sqlTool.repository.Projection;

import java.util.List;

public class Where implements Command {
    private final List<Predicate> predicates;

    public Where(List<Predicate> predicates) {
        this.predicates = predicates;
    }

    @Override
    public Table run(Table table, Projection projection) {
        var data = table.data().stream()
                .filter(row -> predicates.stream().allMatch(p -> p.test(table.columns(), row)))
                .toList();
        return new Table(table.name(), table.columns(), data);
    }
}
