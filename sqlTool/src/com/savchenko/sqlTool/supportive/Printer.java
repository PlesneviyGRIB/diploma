package com.savchenko.sqlTool.supportive;

import com.savchenko.sqlTool.model.Table;
import com.savchenko.sqlTool.repository.Projection;

import java.util.List;

public class Printer {
    private static final Integer COLUMN_WIDTH = 20;
    private final Projection projection;

    public Printer(Projection projection) {
        this.projection = projection;
    }

    public void print(Table table) {
        var data = table.getData();
        var res = new StringBuilder();
        for(List<String> row: data) {
            res.append("|");
            for (String entry: row) {
                res.append(String.format("%" + COLUMN_WIDTH + "." + COLUMN_WIDTH + "s", entry)).append("|");
            }
            res.append("\n");
        }
        System.out.printf("Table '%s' [%d rows]\n", table.getName().toUpperCase(), data.size());
        System.out.println(res);
    }
}
