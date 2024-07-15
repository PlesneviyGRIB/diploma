package com.core.sqlTool.utils.printer;

import static java.lang.String.format;

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

    protected String green(Object object) {
        return format("\u001B[32m%s\u001B[0m", object);
    }

    protected String red(Object object) {
        return format("\u001B[31m%s\u001B[0m", object);
    }

    protected String blue(Object object) {
        return format("\u001B[34m%s\u001B[0m", object);
    }

}
