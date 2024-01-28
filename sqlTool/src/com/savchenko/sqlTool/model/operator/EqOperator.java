package com.savchenko.sqlTool.model.operator;

public enum EqOperator implements Operator {
    EQ("="), NOT_EQ("!="), GREATER_OR_EQ(">="),
    LESS_OR_EQ("<="), GREATER(">"), LESS("<");
    public final String designator;
    EqOperator(String designator){
        this.designator = designator;
    }
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
