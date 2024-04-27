package tests;

import com.savchenko.sqlTool.exception.ColumnNotFoundException;
import com.savchenko.sqlTool.exception.ComputedTypeException;
import com.savchenko.sqlTool.exception.IncorrectOperatorUsageException;
import com.savchenko.sqlTool.model.domain.ExternalHeaderRow;
import com.savchenko.sqlTool.model.expression.*;
import com.savchenko.sqlTool.model.visitor.ExpressionValidator;
import com.savchenko.sqlTool.model.domain.Column;
import com.savchenko.sqlTool.query.Q;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.List;

import static com.savchenko.sqlTool.model.operator.Operator.*;

public class ExpressionVisitorTest extends TestBase {
    @Test
    public void invalidOperator() {
        var validator = new ExpressionValidator(List.of(), ExternalHeaderRow.empty());
        var num = new IntegerNumber(1);
        expectError(() -> Q.op(EQ, num).accept(validator), IncorrectOperatorUsageException.class);
        expectError(() -> Q.op(EQ, num, Q.op(IN, num)).accept(validator), IncorrectOperatorUsageException.class);
        expectError(() -> Q.op(EXISTS, Q.op(OR, num)).accept(validator), IncorrectOperatorUsageException.class);
    }

    @Test
    public void invalidColumnReference() {
        var validator = new ExpressionValidator(List.of(new Column("id", "table", IntegerNumber.class)), ExternalHeaderRow.empty());
        var num = new IntegerNumber(1);
        Q.op(EQ, num, Q.column("table", "id")).accept(validator);
        expectError(() -> Q.op(EQ, num, Q.column("table", "ids")).accept(validator), ColumnNotFoundException.class);
    }

    @Test
    public void invalidOparantType() {
        var validator = new ExpressionValidator(List.of(new Column("int", "t", IntegerNumber.class), new Column("bool", "t", BooleanValue.class)), ExternalHeaderRow.empty());
        Q.op(EQ, new IntegerNumber(1), Q.column("t", "int")).accept(validator);
        Q.op(NOT_EQ, new BooleanValue(false), Q.op(EQ, new IntegerNumber(1), Q.column("t", "int"))).accept(validator);
        Q.op(EQ, new IntegerNumber(1), Q.column("t", "int")).accept(validator);
        expectError(() -> Q.op(EQ, new BooleanValue(false), Q.column("t", "int")).accept(validator), ComputedTypeException.class);
        expectError(() -> Q.op(IN, new BooleanValue(false), Q.column("t", "int")).accept(validator), ComputedTypeException.class);
        expectError(() -> Q.op(EQ,
                        Q.op(AND, new BooleanValue(false), Q.op(EQ, new IntegerNumber(1), Q.column("t", "int"))),
                        new TimestampValue(new Timestamp(System.currentTimeMillis()))).accept(validator), ComputedTypeException.class);
    }

    @Test
    public void expressionPrinter() {
        var expression = Q.op(
                OR,
                Q.op(
                        LESS_OR_EQ,
                        Q.column("actions", "id"),
                        Q.op(MULTIPLY, new LongNumber(1L), new LongNumber(1042L))
                ),
                Q.op(EQ, Q.column("actions", "action_id"), new StringValue("addRow"))
        );
        var expected = "(COLUMN[actions.id] <= (1L * 1042L)) OR (COLUMN[actions.action_id] = addRow)";
        Assert.assertEquals(expected, expression.stringify());
    }

    @Test
    public void injectorTest() {

    }
}
