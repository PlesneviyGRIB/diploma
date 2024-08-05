package com.core.sqlTool.model.command;

import com.core.sqlTool.model.complexity.CalculatorEntry;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.domain.Projection;
import com.core.sqlTool.model.resolver.Resolver;


public sealed interface Command permits ConstructIndexCommand, DistinctCommand, FromCommand, GroupByCommand, LimitCommand, OffsetCommand, OrderByCommand, SelectCommand, TableAliasCommand, WhereCommand, JoinCommand {

    LazyTable run(LazyTable lazyTable, Projection projection, Resolver resolver, CalculatorEntry calculatorEntry);

    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {

        T visit(ConstructIndexCommand command);

        T visit(DistinctCommand command);

        T visit(FromCommand command);

        T visit(GroupByCommand command);

        T visit(LimitCommand command);

        T visit(OffsetCommand command);

        T visit(OrderByCommand command);

        T visit(SelectCommand command);

        T visit(TableAliasCommand command);

        T visit(WhereCommand command);

        T visit(JoinCommand command);

    }

}
