package com.savchenko.sqlTool.model;

import java.util.LinkedList;
import java.util.List;

public class Table {
    private final String name;
    private final List<List<String>> data = new LinkedList<>();

    public Table(String name) {
        this.name = name;
    }

    public void addRow(List<String> row){
        data.add(row);
    }
}
