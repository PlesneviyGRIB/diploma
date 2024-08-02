package com.core.sqlTool.model.resolver;

import com.core.sqlTool.model.cache.CacheContext;
import com.core.sqlTool.model.command.Command;
import com.core.sqlTool.model.complexity.Calculator;
import com.core.sqlTool.model.complexity.CalculatorEntry;
import com.core.sqlTool.model.domain.ExternalHeaderRow;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.domain.Projection;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public record Resolver(Projection projection, CacheContext cacheContext) {

    public ResolverResult resolve(List<Command> commands, ExternalHeaderRow externalRow) {

        var calculator = new Calculator(new LinkedList<>());

        var targetTable = commands.stream().reduce(
                new LazyTable(null, List.of(), Stream.of(), externalRow),
                (table, command) -> {

                    var calculatorEntry = new CalculatorEntry(command);
                    calculator.entries().add(calculatorEntry);

                    return command.run(table, projection, this, calculatorEntry);
                },
                (lazyTable1, lazyTable2) -> lazyTable2
        );

        return new ResolverResult(targetTable, calculator);
    }

}
