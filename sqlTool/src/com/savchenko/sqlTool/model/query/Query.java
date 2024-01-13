package com.savchenko.sqlTool.model.query;

import com.savchenko.sqlTool.model.Builder;
import com.savchenko.sqlTool.model.operation.From;
import com.savchenko.sqlTool.model.operation.Operation;
import com.savchenko.sqlTool.model.operation.Select;

import java.util.LinkedList;
import java.util.List;

public class Query implements Builder<List<Operation>> {
    private final List<Operation> operations = new LinkedList<>();
    private Query(){}

    public static Query create(){
        return new Query();
    }

    public Query select(List<String> columns) {
        operations.add(new Select(columns));
        return this;
    }

    public Query from(List<String> tables){
        operations.add(new From(tables));
        return this;
    }

    @Override
    public List<Operation> build() {
        return operations;
    }
}
