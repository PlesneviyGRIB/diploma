package com.savchenko.sqlTool.utils;

import com.savchenko.sqlTool.model.structure.Table;
import org.apache.commons.collections4.ListUtils;

import java.util.List;

public class SqlUtils {
    public static Table cartesianProduct(Table table1, Table table2) {
        var data1 = table1.data();
        var data2 = table2.data();
        var data = data1.stream().flatMap(prefix -> data2.stream().map(postfix -> ListUtils.union(prefix, postfix))).toList();
        return new Table(null, ListUtils.union(table1.columns(), table2.columns()), data, List.of());
    }
}
