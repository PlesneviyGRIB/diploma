package com.core.sqlTool.model.complexity;

import com.core.sqlTool.model.command.Command;
import com.core.sqlTool.model.domain.Row;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class CalculatorEntry {

    private final Command command;

    private final AtomicInteger counter = new AtomicInteger(0);

    public void count(Row row) {
        counter.incrementAndGet();
    }

}
