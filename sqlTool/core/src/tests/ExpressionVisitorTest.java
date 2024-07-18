package tests;

public class ExpressionVisitorTest extends TestBase {
//    @Test
//    public void invalidOperator() {
//        var validator = new ExpressionValidator(List.of(), ExternalHeaderRow.empty());
//        var num = new Number(1);
//        expectError(() -> Q.op(EQ, num).accept(validator), IncorrectOperatorUsageException.class);
//        expectError(() -> Q.op(EQ, num, Q.op(IN, num)).accept(validator), IncorrectOperatorUsageException.class);
//        expectError(() -> Q.op(EXISTS, Q.op(OR, num)).accept(validator), IncorrectOperatorUsageException.class);
//    }
//
//    @Test
//    public void invalidColumnReference() {
//        var validator = new ExpressionValidator(List.of(new Column("id", "tableName", Number.class)), ExternalHeaderRow.empty());
//        var num = new Number(1);
//        Q.op(EQ, num, Q.columnName("tableName", "id")).accept(validator);
//        expectError(() -> Q.op(EQ, num, Q.columnName("tableName", "ids")).accept(validator), ColumnNotFoundException.class);
//    }
//
//    @Test
//    public void invalidOparantType() {
//        var validator = new ExpressionValidator(List.of(new Column("int", "t", Number.class), new Column("bool", "t", BooleanValue.class)), ExternalHeaderRow.empty());
//        Q.op(EQ, new Number(1), Q.columnName("t", "int")).accept(validator);
//        Q.op(NOT_EQ, new BooleanValue(false), Q.op(EQ, new Number(1), Q.columnName("t", "int"))).accept(validator);
//        Q.op(EQ, new Number(1), Q.columnName("t", "int")).accept(validator);
//        expectError(() -> Q.op(EQ, new BooleanValue(false), Q.columnName("t", "int")).accept(validator), ComputedTypeException.class);
//        expectError(() -> Q.op(IN, new BooleanValue(false), Q.columnName("t", "int")).accept(validator), ComputedTypeException.class);
//        expectError(() -> Q.op(EQ,
//                        Q.op(AND, new BooleanValue(false), Q.op(EQ, new Number(1), Q.columnName("t", "int"))),
//                        new TimestampValue(new Timestamp(System.currentTimeMillis()))).accept(validator), ComputedTypeException.class);
//    }
//
//    @Test
//    public void expressionPrinter() {
//        var expression = Q.op(
//                OR,
//                Q.op(
//                        LESS_OR_EQ,
//                        Q.columnName("actions", "id"),
//                        Q.op(MULTIPLY, new LongNumber(1L), new LongNumber(1042L))
//                ),
//                Q.op(EQ, Q.columnName("actions", "action_id"), new StringValue("addRow"))
//        );
//        var expected = "(COLUMN[actions.id] <= (1L * 1042L)) OR (COLUMN[actions.action_id] = addRow)";
//        Assert.assertEquals(expected, expression.stringify());
//    }
//
//    @Test
//    public void injectorTest() {
//
//    }
}
