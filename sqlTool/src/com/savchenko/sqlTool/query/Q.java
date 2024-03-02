package com.savchenko.sqlTool.query;

import com.savchenko.sqlTool.model.command.Order;
import com.savchenko.sqlTool.model.expression.BinaryOperation;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.expression.TernaryOperation;
import com.savchenko.sqlTool.model.expression.UnaryOperation;
import com.savchenko.sqlTool.model.operator.Operator;
import com.savchenko.sqlTool.model.domain.Column;

public class Q {
    public static UnaryOperation op(Operator operator, Expression expression) {
        return new UnaryOperation(operator, expression);
    }
    public static BinaryOperation op(Operator operator, Expression left, Expression right) {
        return new BinaryOperation(operator, left, right);
    }
    public static TernaryOperation op(Operator operator, Expression first, Expression second, Expression third) {
        return new TernaryOperation(operator, first, second, third);
    }
    public static Column column(String table, String column) {
        return new Column(column, table, null);
    }
    public static Order order(Column column) {
        return new Order(column, false);
    }
    public static Order order(Column column, boolean reverse) {
        return new Order(column, reverse);
    }
}