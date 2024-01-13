package com.savchenko.sqlTool.supportive;

import com.savchenko.sqlTool.model.Table;

import java.util.ArrayList;

public class Utils {
    public static Table renameTable(Table table, String tableName) {
        var res = new Table(tableName);
        table.getData().forEach(row -> res.addRow(new ArrayList<>(row)));
        return res;
    }
}
