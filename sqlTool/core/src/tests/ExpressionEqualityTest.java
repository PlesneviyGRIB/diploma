package tests;

import com.core.sqlTool.model.command.SelectCommand;
import com.core.sqlTool.model.command.WhereCommand;
import com.core.sqlTool.model.domain.Column;
import com.core.sqlTool.model.expression.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.client.sqlTool.expression.Operator.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ExpressionEqualityTest {

    @Test
    public void nullValueEquality() {

        var expression1 = new NullValue();
        var expression2 = new NullValue();

        assertEquals(expression1, expression2);
        assertNotEquals(expression1, new BooleanValue(true));
    }

    @Test
    public void columnEquality() {

        var expression1 = new Column("entity", "id", NumberValue.class);
        var expression2 = new Column("entity", "id", NumberValue.class);

        assertEquals(expression1, expression2);

        expression1 = new Column("entity", "id", NumberValue.class);
        expression2 = new Column("entity", "id", null);

        assertEquals(expression1, expression2);

        expression1 = new Column("entity", "id", NumberValue.class);
        expression2 = new Column("entity", "id", BooleanValue.class);

        assertEquals(expression1, expression2);
    }

    @Test
    public void unaryOperationEquality() {

        var expression1 = new UnaryOperation(NOT, new NullValue());
        var expression2 = new UnaryOperation(NOT, new NullValue());

        assertEquals(expression1, expression2);
    }

    @Test
    public void binaryOperationEquality() {

        var expression1 = new BinaryOperation(AND, new NullValue(), new BooleanValue(false));
        var expression2 = new BinaryOperation(AND, new NullValue(), new BooleanValue(false));

        assertEquals(expression1, expression2);
    }

    @Test
    public void ternaryOperationEquality() {

        var expression1 = new TernaryOperation(BETWEEN, new NullValue(), new StringValue("a"), new StringValue("b"));
        var expression2 = new TernaryOperation(BETWEEN, new NullValue(), new StringValue("a"), new StringValue("b"));

        assertEquals(expression1, expression2);
    }

    @Test
    public void expressionListEquality() {

        var expression1 = new ExpressionList(List.of(new NumberValue(1), new NumberValue(1)));
        var expression2 = new ExpressionList(List.of(new NumberValue(1), new NumberValue(1)));

        assertEquals(expression1, expression2);
    }

    @Test
    public void subTableEquality() {

        var expression1 = new SubQuery(List.of(new SelectCommand(List.of()), new WhereCommand(new NullValue())));
        var expression2 = new SubQuery(List.of(new SelectCommand(List.of()), new WhereCommand(new NullValue())));

        assertEquals(expression1, expression2);
    }

    @Test
    public void expressionEquality() {

        var expression1 = new TernaryOperation(BETWEEN,
                new Column("entity", "id", NumberValue.class),
                new NumberValue(0), new NumberValue(0)
        );

        var expression2 = new TernaryOperation(BETWEEN,
                new Column("entity", "id", NumberValue.class),
                new NumberValue(0), new NumberValue(0)
        );

        assertEquals(expression1, expression2);
    }

}
