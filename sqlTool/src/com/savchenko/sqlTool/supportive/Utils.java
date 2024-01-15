package com.savchenko.sqlTool.supportive;

import com.savchenko.sqlTool.model.Table;

import java.util.ArrayList;

public class Utils {
    public static Table renameTable(Table table, String tableName) {
        return new Table(tableName, table.columns(), table.data());
    }
}
