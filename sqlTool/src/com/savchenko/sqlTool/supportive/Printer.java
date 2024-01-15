package com.savchenko.sqlTool.supportive;

import com.savchenko.sqlTool.model.Column;
import com.savchenko.sqlTool.model.Table;
import com.savchenko.sqlTool.repository.Projection;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

public class Printer {
    private static final Integer COLUMN_WIDTH = 15;
    private final Projection projection;

    public Printer(Projection projection) {
        this.projection = projection;
    }

    public void print(Table table) {
        var res = new StringBuilder();
        var data = table.data();

        Function<String, String> formatCell = entry -> String.format("%" + COLUMN_WIDTH + "." + COLUMN_WIDTH + "s", entry);
        Runnable emptyRow = () -> {
            res.append("+");
            for (int i = 0; i < table.columns().size(); i++){
                for (int j = 0; j < COLUMN_WIDTH; j++){
                    res.append("-");
                }
                res.append("+");
            }
            res.append("\n");
        };

        emptyRow.run();
        res.append("|");
        for (Column column: table.columns()){
            res.append(formatCell.apply(column.name())).append("|");
        }
        res.append("\n");
        emptyRow.run();
        for(List<String> row: data) {
            res.append("|");
            for (String entry: row) {
                res.append(formatCell.apply(entry)).append("|");
            }
            res.append("\n");
        }
        emptyRow.run();

        System.out.printf("Table '%s' [%d rows]\n", table.name().toUpperCase(), data.size());
        System.out.println(res);
    }
}
