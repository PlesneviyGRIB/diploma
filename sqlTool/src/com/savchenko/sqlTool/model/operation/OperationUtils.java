package com.savchenko.sqlTool.model.operation;

import com.savchenko.sqlTool.model.Table;
import org.apache.commons.collections4.ListUtils;

public class OperationUtils {
    public static Table cartesianProduct(Table table1, Table table2) {
        var res = new Table(null);
        var data1 = table1.getData();
        var data2 = table2.getData();
        data2.forEach(postfix -> data1.forEach(prefix -> res.addRow(ListUtils.union(prefix, postfix))));
        return res;
    }
}
