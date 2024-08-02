package tests;

import com.client.sqlTool.command.Order;
import com.client.sqlTool.domain.Column;
import com.client.sqlTool.expression.Number;
import com.client.sqlTool.expression.String;
import com.client.sqlTool.expression.*;
import com.client.sqlTool.query.Query;
import com.core.sqlTool.model.domain.ExternalHeaderRow;
import com.core.sqlTool.model.domain.HeaderRow;
import com.core.sqlTool.model.visitor.ExpressionCalculator;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Timestamp;

import static com.client.sqlTool.expression.Operator.*;

public class ExpressionComplexityCalculationTest extends TestBase {

    @Test
    public void valueComplexityCalculation() {
        calculate(Bool.TRUE, 0, Bool.TRUE);
        calculate(FloatNumber.of(1), 0, FloatNumber.of(1));
        calculate(Null.VALUE, 0, Null.VALUE);
        calculate(Number.of(1), 0, Number.of(1));
        calculate(String.of("1"), 0, String.of("1"));
        calculate(TimeStamp.of(new Timestamp(1)), 0, TimeStamp.of(new Timestamp(1)));
    }

    @Test
    public void unaryOperationCalculation() {
        calculate(Unary.of(IS_NULL, Null.VALUE), 1, Bool.TRUE);
        calculate(Unary.of(IS_NOT_NULL, Null.VALUE), 1, Bool.FALSE);
        calculate(Unary.of(NOT, Bool.TRUE), 1, Bool.FALSE);
        calculate(Unary.of(EXISTS, Query.from("courses")), 1, Bool.TRUE);
    }

    @Test
    public void binaryOperationCalculation() {
        calculate(Binary.of(AND, Bool.TRUE, Bool.FALSE), 1, Bool.FALSE);
        calculate(Binary.of(IN, Number.of(3), List.of(Number.of(1), Number.of(2), Number.of(3))), 3, Bool.TRUE);
        calculate(Binary.of(EQ, String.of("12345"), String.of("12345")), 1, Bool.TRUE);
        calculate(Binary.of(LIKE, String.of("1234567"), String.of("???45%")), 7, Bool.TRUE);
    }

    @Test
    public void ternaryOperationCalculation() {
        calculate(Ternary.of(BETWEEN, Number.of(2), Number.of(1), Number.of(3)), 2, Bool.TRUE);
        calculate(Ternary.of(BETWEEN, String.of("b"), String.of("a"), String.of("c")), 2, Bool.TRUE);
    }

    @Test
    public void lazinessOperationCalculation() {
        calculate(Binary.of(AND, Bool.FALSE, Binary.of(OR, Bool.TRUE, Bool.TRUE)), 1, Bool.FALSE);
        calculate(Binary.of(AND, Bool.TRUE, Binary.of(AND, Bool.TRUE, Binary.of(OR, Bool.FALSE, Bool.TRUE))), 3, Bool.TRUE);
        calculate(Binary.of(IN, Number.of(160), Query.from("courses").orderBy(Order.of(Column.of("id")).desc()).select(Column.of("id"))), 1, Bool.TRUE);
        calculate(Binary.of(IN, Number.of(160), Query.from("courses").orderBy(Order.of(Column.of("id")).asc()).select(Column.of("id"))), 14, Bool.TRUE);
    }

    private void calculate(Expression expression, Integer complexity, Expression result) {

        var domainExpression = convertExpression(expression);
        var domainExpressionResult = convertExpression(result);

        var calculatedExpressionResult = domainExpression.accept(new ExpressionCalculator(resolver, HeaderRow.empty(), ExternalHeaderRow.empty()));

        Assert.assertEquals(complexity, calculatedExpressionResult.getComplexity());
        Assert.assertEquals(domainExpressionResult, calculatedExpressionResult.getValue());
    }

}
