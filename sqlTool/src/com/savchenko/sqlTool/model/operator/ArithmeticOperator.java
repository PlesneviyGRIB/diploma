package com.savchenko.sqlTool.model.operator;

public enum ArithmeticOperator implements Operator {
    PLUS("+"), MINUS("-"), MULTIPLY("*"),
    DIVISION("/"), MOD("%");
    public final String designator;
    ArithmeticOperator(String designator) {
        this.designator = designator;
    }
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
