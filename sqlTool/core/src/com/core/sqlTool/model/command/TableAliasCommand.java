package com.core.sqlTool.model.command;

import com.core.sqlTool.model.complexity.CalculatorEntry;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.domain.Projection;
import com.core.sqlTool.model.resolver.Resolver;
import com.core.sqlTool.utils.ModelUtils;

public record TableAliasCommand(String alias) implements Command {

    @Override
    public LazyTable run(LazyTable lazyTable, Projection projection, Resolver resolver, CalculatorEntry calculatorEntry) {
        return ModelUtils.renameTable(lazyTable, alias);
    }

    @Override
    public  <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}
