package tests;

import com.savchenko.sqlTool.model.expression.ExpressionValidationVisitor;
import com.savchenko.sqlTool.model.expression.IntegerNumber;
import com.savchenko.sqlTool.query.Q;
import org.junit.Test;

import java.util.List;

import static com.savchenko.sqlTool.model.operator.Operator.*;

public class ExpressionValidationTest extends TestBase {
    @Test
    public void invalidOperator() {
        var validator = new ExpressionValidationVisitor(List.of());
        var num = new IntegerNumber(1);
        expectError(() -> Q.op(EQ, num).accept(validator));
        expectError(() -> Q.op(EQ, num, Q.op(IN, num)).accept(validator));
        expectError(() -> Q.op(EXISTS, Q.op(OR, num)).accept(validator));
    }
}
