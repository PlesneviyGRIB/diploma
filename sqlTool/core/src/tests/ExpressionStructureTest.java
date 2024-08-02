package tests;

import com.client.sqlTool.expression.Number;
import com.client.sqlTool.expression.*;
import com.core.sqlTool.exception.ComputedTypeException;
import com.core.sqlTool.exception.IncorrectOperatorUsageException;
import com.core.sqlTool.model.domain.ExternalHeaderRow;
import com.core.sqlTool.model.visitor.ExpressionResultTypeResolver;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.List;

import static com.client.sqlTool.expression.Operator.*;

public class ExpressionStructureTest extends TestBase {

    @Test
    public void invalidOperator() {
        expectIncorrectOperatorUsageException(Unary.of(EQ, Number.of(1)));
        expectIncorrectOperatorUsageException(Unary.of(BETWEEN, Number.of(1)));

        expectIncorrectOperatorUsageException(Binary.of(EXISTS, Number.of(1), Number.of(1)));
        expectIncorrectOperatorUsageException(Binary.of(BETWEEN, Number.of(1), Number.of(1)));

        expectIncorrectOperatorUsageException(Ternary.of(IN, Number.of(1), Number.of(1), Number.of(1)));
        expectIncorrectOperatorUsageException(Ternary.of(NOT, Number.of(1), Number.of(1), Number.of(1)));
    }

    @Test
    public void invalidOperandType() {
        expectComputedTypeException(Binary.of(EQ, Bool.FALSE, Number.of(1)));
        expectComputedTypeException(Binary.of(IN, Bool.FALSE, com.client.sqlTool.expression.List.of(Number.of(1))));
        expectComputedTypeException(Binary.of(EQ,
                Binary.of(AND, Bool.FALSE, Binary.of(EQ, Number.of(1), Number.of(2))),
                TimeStamp.of(new Timestamp(1)))
        );
    }

    private void expectIncorrectOperatorUsageException(Expression expression) {
        var validator = new ExpressionResultTypeResolver(List.of(), ExternalHeaderRow.empty());
        expectException(() -> convertExpression(expression).accept(validator), IncorrectOperatorUsageException.class);
    }

    private void expectComputedTypeException(Expression expression) {
        var validator = new ExpressionResultTypeResolver(List.of(), ExternalHeaderRow.empty());
        expectException(() -> convertExpression(expression).accept(validator), ComputedTypeException.class);
    }

}
