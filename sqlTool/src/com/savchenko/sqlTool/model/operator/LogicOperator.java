package com.savchenko.sqlTool.model.operator;

public enum LogicOperator implements Operator {
    AND("and"), ANY("any"), BETWEEN("between"),
    EXISTS("exists"), IN("in"), OR("or"),
    IS_NULL("is null"), UNIQUE("unique");
    public final String designator;
    LogicOperator(String designator) {
        this.designator = designator;
    }
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
