package com.savchenko.sqlTool.utils.printer;

public abstract class Printer<T> {

    protected final T domain;

    protected final StringBuilder sb;

    public Printer(T domain) {
        this.domain = domain;
        this.sb = new StringBuilder();
    }

    public String stringify() {
        if (!sb.isEmpty()) {
            return sb.toString();
        }
        buildString();
        return sb.toString();
    }

    protected abstract void buildString();
}
